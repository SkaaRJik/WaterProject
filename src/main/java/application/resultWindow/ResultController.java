package application.resultWindow;

import application.ApplicationFactory;
import application.ValidationChangeListener;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import model.Model;
import model.pipe.BasePipe;
import org.apache.log4j.Logger;
import org.apache.poi.xwpf.usermodel.*;
import utils.Validator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SkaaRJ on 03.04.2018.
 */
public class ResultController {
    Logger logger = Logger.getLogger(ResultController.class);

    @FXML
    TableView<Double[]> tableView;
    @FXML
    TextField textFieldTime;
    @FXML
    Label labelMessage;
    @FXML
    TextArea textArea;
    @FXML
    ProgressBar modellingProgressBar;
    @FXML
    Button saveReportButton;
    @FXML
    private GridPane timeControlGrid;

    Model model;

    DoubleProperty maxValue = null;

    double currentTime = 0;
    


    public void updateModel(Model model) {
        this.model = model;
        this.textArea.setEditable(false);
        this.tableView.getColumns().clear();
        this.tableView.getItems().clear();

        this.timeControlGrid.setVisible(false);


        maxValue = null;

        TableColumn<Double[], Double> tableColumn;
        for (int i = 0; i < model.getRows()-1; i++) {
            if(i == 0) {
                tableColumn = new TableColumn("Время");
            }
            else {
                tableColumn = new TableColumn(String.valueOf(i));
            }
            int finalI = i;
            tableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Double[], Double>, ObservableValue<Double>>() {
                @Override
                public ObservableValue<Double> call(TableColumn.CellDataFeatures<Double[], Double> param) {
                    return new SimpleObjectProperty<Double>((param.getValue()[finalI]));
                }
            });

            tableColumn.setCellFactory(tc -> new TableCell<Double[], Double>(){
                @Override
                protected void updateItem(Double item, boolean empty) {

                    super.updateItem(item, empty);
                    Platform.runLater(() -> {
                        if(item != null && maxValue != null) {
                            if(getTableColumn().getText().matches("[\\d]")) {
                                double v = (item - model.getBackgroundConcetration()) / (maxValue.get() - model.getBackgroundConcetration());
                                if (v == Double.NaN) v = 1;
                                setBackground(new Background(new BackgroundFill(Color.rgb(255, 0, 0, v), CornerRadii.EMPTY, Insets.EMPTY)));
                            }
                        }
                    });
                    this.setText(String.valueOf(item));
                }
            });
            this.tableView.getColumns().add(tableColumn);
        }

        tableView.setItems(model.getResults());

        this.textFieldTime.textProperty().addListener(new ValidationChangeListener(this.textFieldTime));

        this.model.statusProperty().addListener((observable, oldValue, newValue) -> {
            if((Double)newValue == 1.0){
                this.saveReportButton.setDisable(false);
            }
        });
        this.modellingProgressBar.progressProperty().bind(this.model.statusProperty());

        /*ChangeListener<String> listener = new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                try {
                    if(newValue != null ) {
                        if(newValue.length() != 0){
                            double time = Double.valueOf(newValue.replace(",","."));
                            if(time % model.getSplitDt() != 0){
                                time = (Math.round(time /  model.getSplitDt()))*model.getSplitDt();
                                labelMessage.setText("Выбрано число кратное шагу среза: " + model.getSplitDt());
                            }
                        }

                    }
                } catch (NumberFormatException ex){
                    ApplicationFactory.getInstance().getAlert("Используте формат цифр 0.0");
                }
            }
        }*/

        textFieldTime.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.ENTER){

                if(textFieldTime.getText() != null ) {
                    if(textFieldTime.getText() .length() != 0){
                        currentTime = Double.valueOf(textFieldTime.getText() .replace(",","."));
                            goToTime(currentTime );
                        }
                    }
            }
        });

        this.model.statusProperty().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> {
                if((Double) newValue >= 1.0){
                    showReport();
                    if(model.getSplits() != null){
                        this.timeControlGrid.setVisible(true);
                    }
                    this.maxValue = new SimpleDoubleProperty(this.model.getCMAX());
                }
            });
        });

        this.textFieldTime.setText("0");
        currentTime = 0;

    }

    public void startTrain(){
        Thread thread = new Thread(model);
        thread.start();
    }

    private void goToTime(double time){
        String text = this.textFieldTime.getText();
        labelMessage.setText("");
        double max = -1;
        if(Validator.isOnlyNumbers(text)){
            if(time > this.model.getEndTime()) {
                labelMessage.setText("Достигли конца моделирования");
                currentTime = model.getEndTime();
            }
            if(time >= 0 && time <= this.model.getEndTime()) {
                currentTime = time;
                if (time % model.getSplitDt() != 0) {
                    currentTime = (int)(Math.round(time /  model.getSplitDt()))*model.getSplitDt();
                    labelMessage.setText("Выбрано число кратное шагу среза: " + model.getSplitDt());
                }

                Double[][] split = model.getSplits().get((int) Math.round(time / model.getSplitDt()));
                Double[] riverMetres = model.getRiverMetres();
                List<Double[]> doubles = new ArrayList<Double[]>(split.length);
                Double[] row;
                for (int i = 0; i < split.length; i++) {
                    row = new Double[split[i].length+1];
                    row[0] = riverMetres[i];
                    for (int j = 1; j < split[i].length+1; j++) {
                       row[j] = split[i][j-1];
                       max = Double.max(split[i][j-1], max);
                    }
                    doubles.add(row);
                }
                this.maxValue.setValue(max);
                this.tableView.getColumns().get(0).setText("Расстояние от КС (м)");
                this.tableView.setItems(FXCollections.observableArrayList(doubles));
            }
            else if(time < 0) {
                labelMessage.setText("Время достигло начало работы!");
                currentTime = 0;
            }
            textFieldTime.setText(String.format("%.0f", currentTime));
        }
        else {
            ApplicationFactory.getInstance().getAlert("Поле должно содержать только цифры!").showAndWait();
        }
    }

    @FXML
    void goBackTime(){
        this.goToTime(currentTime-model.getSplitDt());
    }

    @FXML
    void goForwardTime(){
        this.goToTime(currentTime+model.getSplitDt());
    }


    @FXML
    public void getResult(){

        this.tableView.getColumns().get(0).setText("Время");
        tableView.setItems(this.model.getResults());

    }


    private ArrayList<Integer> calculateShortcutResult(){
        ArrayList<Integer> shortcutResultIndexes = new ArrayList<Integer>();
        ObservableList<Double[]> results = model.getResults();
        shortcutResultIndexes.add(1);
        for (int time = 2; time < results.size()-1; time++) {
            for (int j = 1; j < results.get(0).length; j++) {
                try {
                    if (results.get(time)[j].doubleValue() != (results.get(shortcutResultIndexes.get(shortcutResultIndexes.size() - 1))[j].doubleValue())) {
                        shortcutResultIndexes.add(time);
                        break;
                    }
                } catch (Exception ex){
                    ex.printStackTrace();
                    logger.error("time = " + time + "j = " + j);
                    logger.error("Last resylt:" + results.get(time)[j]);
                }
            }
        }
        if(results.size()-1!=shortcutResultIndexes.get(shortcutResultIndexes.size()-1)) {
            shortcutResultIndexes.add(results.size()-1);
        }
        return shortcutResultIndexes;
    }

    void showReport(){
        StringBuilder result = new StringBuilder("CMAX = " + String.format("%."+this.model.getRound()+"f", model.getCMAX()) + "\t\t " +
                "P = " + String.format("%."+this.model.getRound()+"f", model.getDegreeOfMixing()) + "%\t\t");
        Double temp = model.getDilutionRatio();
        if(temp != null) result.append("N = " + String.format("%."+this.model.getRound()+"f", temp) + "\n");
        result.append("CMAX\\СПДК = " + String.format("%."+this.model.getRound()+"f", model.getCMAXDevidedByPDK()) + "\t\t");
        temp = model.getCCTD();
        if(temp != null) result.append("ССТД= " + String.format("%."+this.model.getRound()+"f", temp) + "\t\t");
        temp = model.getNDS();
        if(temp != null) result.append("НДС= " + String.format("%."+this.model.getRound()+"f", temp) + "\t\t");
        this.textArea.setText(result.toString());
    }


    @FXML
    public void saveReport() throws IOException {
        FileChooser fileChooser = new FileChooser();//Класс работы с диалогом выборки и сохранения
        fileChooser.setTitle("Save Document");//Заголовок диалога


        fileChooser.getExtensionFilters().addAll( new FileChooser.ExtensionFilter("DOC files (*.docx)", "*.docx"),
                new FileChooser.ExtensionFilter("TEXT files (*.txt)", "*.txt"));//Расширение));//Расширение);

        fileChooser.setInitialDirectory(new File("").getAbsoluteFile());
        File file = fileChooser.showSaveDialog(this.tableView.getScene().getWindow());//Указываем текущую сцену
        if (file != null) {


            if(fileChooser.getSelectedExtensionFilter().equals(fileChooser.getExtensionFilters().get(1))){
                if (!file.getPath().endsWith(".txt")) {
                    file = new File(file.getPath() + ".txt");
                }
                this.createReportTXT(file);
            }
            else if(fileChooser.getSelectedExtensionFilter().equals(fileChooser.getExtensionFilters().get(0))){
                if (!file.getPath().endsWith(".docx")) {
                    file = new File(file.getPath() + ".docx");
                }
                this.createReportDoc(file);
            }


        }
    }


    private void createReportDoc(File file){

        ArrayList<Integer> shortcutResultIndexes = this.calculateShortcutResult();
        XWPFDocument document= new XWPFDocument();
        try {
            FileOutputStream out = new FileOutputStream(file);


            createDocParagraph(document,"Данные о модели за " + this.model.getEndTime() + " секунд.");
            createDocParagraph(document,"Шаг по ширине реки(dy): " + String.format("%.2f",this.model.getDy()[0]) + " м.");
            createDocParagraph(document,"Шаг по длине реки(dx): " + String.format("%.2f",this.model.getDx()) + " м.");
            createDocParagraph(document,"Шаг по времени(dt): " + String.format("%.2f",this.model.getDt()) + " сек.");
            createDocParagraph(document,"");
            createDocParagraph(document,"Данные о реке:");
            createDocParagraph(document,"LB - Средняя ширина (м.): " +this.model.getRiverInfo().riverWidth + "");
            createDocParagraph(document,"LH - Средняя глубина (м.): " +this.model.getRiverInfo().riverDepth +"");
            createDocParagraph(document,"Средняя скорость течения (м/c): " +this.model.getRiverInfo().flowSpeed +"");
            createDocParagraph(document,"Коэффициент поперечной диффузии: " +this.model.getRiverInfo().diffusion +"");
            createDocParagraph(document,"Коэффициент неконсервативности: " +this.model.getRiverInfo().coefficientOfNonConservatism +"");
            createDocParagraph(document,"");
            createDocParagraph(document,"Данные о веществе:");
            createDocParagraph(document,"Наименование: " + this.model.getSubstance().getName()  + "");
            createDocParagraph(document,"Доля в ЛПВ: " + this.model.getSubstance().getProportion()  + "");
            createDocParagraph(document,"Значение CPDK: " + this.model.getSubstance().getLAC()  + "");
            createDocParagraph(document,"Фоновая концентрация: " + this.model.getRiverInfo().backgroundConcentration  + "");
            createDocParagraph(document,"");
            createDocParagraph(document,"Данные о работающих трубах:");
            {
                int ind = 1;
                for (BasePipe pipe : this.model.getPipes()) {
                    createDocParagraph(document,"Параметры выпуска " + ind++ + "");
                    String[] split = pipe.toString().split("\n");
                    for (String str : split){
                        createDocParagraph(document,str);
                    }

                }
            }
            
            XWPFTable table = document.createTable();
            XWPFTableRow tableRowOne = table.getRow(0);

            ObservableList<Double[]> results = this.model.getResults();

            for (int i = 0 ; i < results.get(0).length ; i++){
                if (i == 0) tableRowOne.getCell(0).setText("Время (c) / (мин)");
                else tableRowOne.addNewTableCell().setText(String.valueOf(i));
            }




            for (int i = 0; i < shortcutResultIndexes.size() - 1; i++) {
                tableRowOne = table.createRow();
                //table.addRow(tableRowOne);
                int indexesStep = (shortcutResultIndexes.get(i + 1) - shortcutResultIndexes.get(i) - 1) / 3;
                for (int j = 0; j < results.get(0).length; j++) {
                    if (j == 0)  tableRowOne.getCell(j).setText(String.format("%."+this.model.getRound()+"f / %."+this.model.getRound()+"f",results.get(shortcutResultIndexes.get(i))[j], (results.get(shortcutResultIndexes.get(i))[j])/60));
                        else tableRowOne.getCell(j).setText(String.format("%."+this.model.getRound()+"f", results.get(shortcutResultIndexes.get(i))[j]));

                }

                if (indexesStep == 0) continue;
                for (int k = 1; k < 3; k++) {
                    tableRowOne = table.createRow();
                    //table.addRow(tableRowOne);
                    for (int j = 0; j < results.get(0).length; j++) {
                        if(j==0) tableRowOne.getCell(j).setText(String.format("%."+this.model.getRound()+"f / %."+this.model.getRound()+"f",results.get(k * indexesStep + shortcutResultIndexes.get(i))[j],results.get(k * indexesStep + shortcutResultIndexes.get(i))[j]/60));
                        else tableRowOne.getCell(j).setText(String.format("%."+this.model.getRound()+"f",results.get(k * indexesStep + shortcutResultIndexes.get(i))[j]));
                    }

                }
            }
            tableRowOne = table.createRow();
            //table.addRow(tableRowOne);
            for (int j = 0; j < results.get(0).length; j++) {
                if(j==0) tableRowOne.getCell(j).setText(String.format("%."+this.model.getRound()+"f / %."+this.model.getRound()+"f", results.get(shortcutResultIndexes.get(shortcutResultIndexes.size() - 1))[j] , results.get(shortcutResultIndexes.get(shortcutResultIndexes.size() - 1))[j] / 60));
                else tableRowOne.getCell(j).setText(String.format("%."+this.model.getRound()+"f", results.get(shortcutResultIndexes.get(shortcutResultIndexes.size() - 1))[j]));

            }

            this.createDocParagraph(document,this.textArea.getText());
            document.write(out);
            out.close();
        } catch (Exception ex){
            ex.printStackTrace();
            ApplicationFactory.getInstance().getAlert("Не удалось открыть файл. Закройте, если он открыт!").showAndWait();
        }
    }

    private void createReportTXT(File file) {
        ObservableList<Double[]> results = this.model.getResults();
        ArrayList<Integer> shortcutResultIndexes = this.calculateShortcutResult();
        FileWriter writer = null;
        try {
            writer = new FileWriter(file.getAbsolutePath(), false);
            writer.close();

            writer = new FileWriter(file.getAbsolutePath(), true);


            writer.write("Данные о модели за " + this.model.getEndTime() + " секунд.\n");
            writer.write("Шаг по ширине реки(dy): " + String.format("%.2f", this.model.getDy()[0]) + " м.\n");
            writer.write("Шаг по длине реки(dx): " + String.format("%.2f", this.model.getDx()) + " м.\n");
            writer.write("Шаг по времени(dt): " + String.format("%.2f", this.model.getDt()) + " сек.\n");
            writer.write("\n");
            writer.write("Данные о реке:\n");
            writer.write("LB - Средняя ширина (м.): " + this.model.getRiverInfo().riverWidth + "\n");
            writer.write("LH - Средняя глубина (м.): " + this.model.getRiverInfo().riverDepth + "\n");
            writer.write("Средняя скорость течения (м/c): " + this.model.getRiverInfo().flowSpeed + "\n");
            writer.write("Коэффициент поперечной диффузии: " + this.model.getRiverInfo().diffusion + "\n");
            writer.write("Коэффициент неконсервативности: " + this.model.getRiverInfo().coefficientOfNonConservatism + "\n");
            writer.write("\n");
            writer.write("Данные о веществе:\n");
            writer.write("Наименование: " + this.model.getSubstance().getName() + "\n");
            writer.write("Доля в ЛПВ: " + this.model.getSubstance().getProportion() + "\n");
            writer.write("Значение CPDK: " + this.model.getSubstance().getLAC() + "\n");
            writer.write("Фоновая концентрация: " + this.model.getRiverInfo().backgroundConcentration + "\n");
            writer.write("\n");
            writer.write("Данные о работающих трубах:\n");
            {
                int ind = 1;
                for (BasePipe pipe : this.model.getPipes()) {
                    writer.write("Параметры выпуска " + ind++ + "\n");
                    writer.write(pipe.toString());
                }
            }


            int maxColumn = String.valueOf(results.get(results.size()-1)[0]).length()+3+this.model.getRound();


            for (int i = 0; i < this.tableView.getColumns().size(); i++) {
                if (i == 0) writer.write(String.format("|%"+(maxColumn+2)+"s", "Время (с)"));
                else writer.write(String.format("|%" + (this.model.getRound() + 3 + this.model.getRound()) + "d|", i));
            }
            writer.write("\n");


            for (int i = 0; i < shortcutResultIndexes.size() - 1; i++) {
                //table.addRow(tableRowOne);
                int indexesStep = (shortcutResultIndexes.get(i + 1) - shortcutResultIndexes.get(i) - 1) / 3;
                for (int j = 0; j < results.get(0).length; j++) {
                    if (i == 0) {
                        writeTimeColumn(writer, results.get(shortcutResultIndexes.get(i))[j],  maxColumn);
                    } else
                        writeWasteColumn(writer, results.get(shortcutResultIndexes.get(i))[j]);

                }
                writer.write("\n");

                if (indexesStep == 0) continue;
                for (int k = 1; k < 3; k++) {
                    for (int j = 0; j < results.get(0).length; j++) {
                        if (i == 0)
                            writeTimeColumn(writer, results.get(k * indexesStep + shortcutResultIndexes.get(i))[j], maxColumn);
                        else
                            writeWasteColumn(writer, results.get(k * indexesStep + shortcutResultIndexes.get(i))[j]);
                    }
                    writer.write("\n");
                }
            }
            for (int j = 0; j < results.get(0).length; j++) {
                if (j == 0)
                    writeTimeColumn(writer, results.get(shortcutResultIndexes.get(shortcutResultIndexes.size() - 1))[j], maxColumn);
                else
                    writeWasteColumn(writer, results.get(shortcutResultIndexes.get(shortcutResultIndexes.size() - 1))[j]);
            }

            writer.write("\n\n");
            writer.write(this.textArea.getText());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void writeTimeColumn(FileWriter writer, Double aDouble, int maxColumn) throws IOException {
        writer.write(String.format("|%"+maxColumn+".2f",  aDouble));
    }
    private void writeWasteColumn(FileWriter writer, Double aDouble) throws IOException {
        writer.write(String.format("|%" + (this.model.getRound() + 3) + "."+this.model.getRound()+"f|",  aDouble));
    }

    private XWPFParagraph createDocParagraph(XWPFDocument docxModel, String text){
        XWPFParagraph bodyParagraph = docxModel.createParagraph();
        XWPFRun paragraphConfig = bodyParagraph.createRun();
        paragraphConfig.setFontSize(16);
        // HEX цвет без решетки #
        paragraphConfig.setText( text  );
        return bodyParagraph;
    }


    public void init() {



    }
}
