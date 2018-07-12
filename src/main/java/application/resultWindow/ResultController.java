package application.resultWindow;

import application.ApplicationFactory;
import application.ValidationChangeListener;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import model.Model;
import model.pipe.BasePipe;
import org.apache.poi.xwpf.usermodel.*;
import utils.Validator;



import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by SkaaRJ on 03.04.2018.
 */
public class ResultController {
    @FXML
    TableView<String[]> tableView;
    @FXML
    TextField textFieldTime;
    @FXML
    Label labelMessage;
    @FXML
    TextArea textArea;

    Model model;
    String[][] result;

    ArrayList<Integer> shortcutResultIndexes;

    public void init(Model model){
        this.model = model;
        this.result = model.getResult();
        this.textArea.setEditable(false);
        ObservableList<String[]> observableList = FXCollections.observableArrayList();
        observableList.addAll(Arrays.asList(result));
        observableList.remove(0);
        for (int i = 0; i < this.result[0].length; i++) {
            TableColumn tableColumn = new TableColumn(this.result[0][i]);

            int columnNumber = i;
            tableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<String[], String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<String[], String> p) {
                    return new SimpleStringProperty((p.getValue()[columnNumber]));
                }
            });
            tableView.getColumns().add(tableColumn);
        }
        tableView.setItems(observableList);
        this.textFieldTime.textProperty().addListener(new ValidationChangeListener(this.textFieldTime));
        textFieldTime.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ENTER)  {
                    goToTime(0);
                }
            }
        });
        this.showReport();

    }

    public void updateModel(Model model) {
        this.model = model;
        this.getResult();
        this.showReport();
    }

    private void goToTime(double incrementer){
        String text = this.textFieldTime.getText();
        labelMessage.setText("");
        if(Validator.isOnlyNumbers(text)){
            double time = Double.parseDouble(text) + incrementer;
            if(time > this.model.getEndTime()) {
                labelMessage.setText("Достигли конца моделирования");
                time = model.getEndTime();
            }
            if(time >= 0 && time <= this.model.getEndTime()) {
                if (time % model.getDt() != 0) {
                    time = time - time%model.getDt();
                    labelMessage.setText("Выбрано время кратное " + model.getDt());
                }
                this.result = this.model.getShot(time);
                this.textFieldTime.setText(String.valueOf(time));

                ObservableList<String[]> observableList = FXCollections.observableArrayList();
                observableList.addAll(Arrays.asList(result));
                observableList.remove(0);
                tableView.getColumns().get(0).setText(this.result[0][0]);
                tableView.setItems(observableList);

            }
            else if(time < 0) {
                labelMessage.setText("Время достигло нуля!");
            }
        }
        else {
            ApplicationFactory.getInstance().getAlert("Поле должно содержать только цифры!").showAndWait();
        }
    }

    @FXML
    void goBackTime(){
        this.goToTime(-model.getDt());
    }

    @FXML
    void goForwardTime(){
        this.goToTime(model.getDt());
    }


    @FXML
    public void getResult(){
        this.result = model.getResult();
        ObservableList<String[]> observableList = FXCollections.observableArrayList();
        observableList.addAll(Arrays.asList(result));
        observableList.remove(0);
        tableView.getColumns().get(0).setText(this.result[0][0]);
        tableView.setItems(observableList);

    }


    private void calculateShortcutResult(){
        this.shortcutResultIndexes = new ArrayList<Integer>(10);
        String[][] resultOut = this.model.getResult();
        this.shortcutResultIndexes.add(1);
        for (int time = 2; time < resultOut.length-1; time++) {
            for (int j = 1; j < resultOut[0].length; j++) {
                if (!resultOut[time][j].equals(resultOut[time - 1][j])) {
                    this.shortcutResultIndexes.add(time);
                    break;
                }
            }
        }
        if(!resultOut[resultOut.length-1][0].equals(resultOut[this.shortcutResultIndexes.get(this.shortcutResultIndexes.size()-1)][0])) {
            this.shortcutResultIndexes.add(resultOut.length-1);
        }
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
        String[][] resultOut = this.model.getResult();
        if(this.shortcutResultIndexes == null) this.calculateShortcutResult();
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
                    createDocParagraph(document,pipe.toString());
                }
            }
            
            XWPFTable table = document.createTable();
            XWPFTableRow tableRowOne = table.getRow(0);

            for (int i = 0; i < resultOut[0].length; i++) {
                if (i == 0) tableRowOne.getCell(0).setText(resultOut[0][0]);
                else tableRowOne.addNewTableCell().setText(resultOut[0][i]);
            }

            for (int i = 0; i < this.shortcutResultIndexes.size() - 1; i++) {
                XWPFTableRow tableRow = table.createRow();
                int indexesStep = (shortcutResultIndexes.get(i + 1) - shortcutResultIndexes.get(i) - 1) / 3;
                for (int j = 0; j < resultOut[0].length; j++) {
                    tableRow.getCell(j).setText(resultOut[this.shortcutResultIndexes.get(i)][j]);
                }
                if (indexesStep == 0) continue;
                for (int k = 1; k < 3; k++) {
                    tableRow = table.createRow();
                    for (int j = 0; j < resultOut[0].length; j++) {
                        tableRow.getCell(j).setText(resultOut[k * indexesStep + this.shortcutResultIndexes.get(i)][j]);
                    }
                }
            }
            XWPFTableRow tableRow = table.createRow();
            for (int j = 0; j < resultOut[0].length; j++) {
                tableRow.getCell(j).setText(resultOut[this.shortcutResultIndexes.get(this.shortcutResultIndexes.size() - 1)][j]);
            }
            this.createDocParagraph(document,this.textArea.getText());
            document.write(out);
            out.close();
        } catch (Exception ex){
            ApplicationFactory.getInstance().getAlert("Не удалось открыть файл. Закройте, если он открыт!").showAndWait();
        }
    }

    private void createReportTXT(File file){
        String[][] resultOut = this.model.getResult();
        if(this.shortcutResultIndexes == null) this.calculateShortcutResult();
        FileWriter writer = null;
        try {
            writer = new FileWriter(file.getAbsolutePath(), false);
            writer.close();

            writer = new FileWriter(file.getAbsolutePath(), true);


            writer.write("Данные о модели за " + this.model.getEndTime() + " секунд.\n");
            writer.write("Шаг по ширине реки(dy): " + String.format("%.2f",this.model.getDy()[0]) + " м.\n");
            writer.write("Шаг по длине реки(dx): " + String.format("%.2f",this.model.getDx()) + " м.\n");
            writer.write("Шаг по времени(dt): " + String.format("%.2f",this.model.getDt()) + " сек.\n");
            writer.write("\n");
            writer.write("Данные о реке:\n");
            writer.write("LB - Средняя ширина (м.): " +this.model.getRiverInfo().riverWidth + "\n");
            writer.write("LH - Средняя глубина (м.): " +this.model.getRiverInfo().riverDepth +"\n");
            writer.write("Средняя скорость течения (м/c): " +this.model.getRiverInfo().flowSpeed +"\n");
            writer.write("Коэффициент поперечной диффузии: " +this.model.getRiverInfo().diffusion +"\n");
            writer.write("Коэффициент неконсервативности: " +this.model.getRiverInfo().coefficientOfNonConservatism +"\n");
            writer.write("\n");
            writer.write("Данные о веществе:\n");
            writer.write("Наименование: " + this.model.getSubstance().getName()  + "\n");
            writer.write("Доля в ЛПВ: " + this.model.getSubstance().getProportion()  + "\n");
            writer.write("Значение CPDK: " + this.model.getSubstance().getLAC()  + "\n");
            writer.write("Фоновая концентрация: " + this.model.getRiverInfo().backgroundConcentration  + "\n");
            writer.write("\n");
            writer.write("Данные о работающих трубах:\n");
            {
                int ind = 1;
                for (BasePipe pipe : this.model.getPipes()) {
                    writer.write("Параметры выпуска " + ind++ + "\n");
                    writer.write(pipe.toString());
                }
            }

            for (int i = 0; i < resultOut[0].length; i++) {
                if(i == 0) writer.write(String.format("|%10s|",resultOut[0][i]));
                else writer.write(String.format(" %7s |",resultOut[0][i]));
            }
            writer.write("\n");
            writer.close();
            writer = new FileWriter(file.getAbsolutePath(), true);
            for (int i = 0; i < this.shortcutResultIndexes.size()-1; i++) {
                int indexesStep = (shortcutResultIndexes.get(i+1) - shortcutResultIndexes.get(i) -1) / 3;
                for (int j = 0; j < resultOut[0].length; j++) {
                    if(j == 0) writer.write(String.format("| %10s |",resultOut[this.shortcutResultIndexes.get(i)][j]));
                    else writer.write(String.format(" %7s |",resultOut[this.shortcutResultIndexes.get(i)][j]));
                }
                writer.write("\n");
                if(indexesStep == 0) continue;
                for (int k = 1; k < 3; k++) {
                    for (int j = 0; j < resultOut[0].length; j++) {
                        if(j == 0) writer.write(String.format("| %10s |",resultOut[k * indexesStep + this.shortcutResultIndexes.get(i)][j]));
                        else writer.write(String.format(" %7s |",resultOut[k * indexesStep + this.shortcutResultIndexes.get(i)][j]));
                    }
                    writer.write("\n");
                }
            }
            for (int j = 0; j < resultOut[0].length; j++) {
                if (j == 0)
                    writer.write(String.format("| %10s |", resultOut[this.shortcutResultIndexes.get(this.shortcutResultIndexes.size() - 1)][j]));
                else
                    writer.write(String.format(" %7s |", resultOut[this.shortcutResultIndexes.get(this.shortcutResultIndexes.size() - 1)][j]));
            }
            writer.write("\n\n");
            writer.write(this.textArea.getText());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private XWPFParagraph createDocParagraph(XWPFDocument docxModel, String text){
        XWPFParagraph bodyParagraph = docxModel.createParagraph();
        XWPFRun paragraphConfig = bodyParagraph.createRun();
        paragraphConfig.setFontSize(16);
        // HEX цвет без решетки #
        paragraphConfig.setText( text  );
        return bodyParagraph;
    }


}
