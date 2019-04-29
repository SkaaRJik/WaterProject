package application;

import application.timeChooserWindow.TimeChooserWindow;
import exceptions.NoPipesException;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Model;
import model.pipe.*;
import model.pipe.mode.*;
import model.river.River;
import model.substance.Substance;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Контроллер графического интерфейса.
 * <p>
 * Отвечает за логичку интерфейса (нажатие клавиш,
 * инициализация элементов)
 */
public class Controller {

    @FXML
    TextField textFieldNonConservatismCoef;
    @FXML
    TextField textFieldDiffusionCoef;
    @FXML
    TextField textFieldFlowSpeed;
    @FXML
    TextField textFieldRiverDepth;
    @FXML
    TextField textFieldRiverWidth;
    @FXML
    TextField textFieldSubstance;
    @FXML
    TextField textFieldProportion;
    @FXML
    TextField textFieldLAC;
    @FXML
    TextField textFieldConcentration;
    @FXML
    TextField textFieldRound;
    @FXML
    Accordion accordionPlant;
    
    @FXML
    TitledPane riverTitlePane;
    @FXML
    TitledPane substanceTitlePane;        


    List<Map<String, Node>> dynamicComponents = new LinkedList<>();

    /**
     *
     * @param index
     * @return
     */

    protected TitledPane createTitledPanePipe(int index){

        Map<String, Node> content = new HashMap<>();

        /* Базовая часть всех труб */
       //TilePane  = new TilePane(Orientation.HORIZONTAL);



        FlowPane flowPane = new FlowPane(Orientation.HORIZONTAL,5, 5);

        AnchorPane root = new AnchorPane(flowPane);
        AnchorPane.setTopAnchor(flowPane, 5.0);
        AnchorPane.setLeftAnchor(flowPane, 5.0);
        AnchorPane.setRightAnchor(flowPane, 5.0);

        flowPane.setPrefWrapLength(Region.USE_COMPUTED_SIZE);
        flowPane.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);

        Label typeLabel = new Label("Тип выпуска");
        ChoiceBox<String> choiceBoxTypeOfPipe = new ChoiceBox(FXCollections.observableArrayList("Береговой сосредоточенный",
                "Береговой распределенный", "Русловой сосредоточенный", "Русловой рассеивающий",
                "Русловой рассеивающий с 2 ветками"));
        choiceBoxTypeOfPipe.getSelectionModel().select("Береговой сосредоточенный");
        content.put("choiceBoxTypeOfPipe", choiceBoxTypeOfPipe);
        VBox typeContainer = new VBox(typeLabel,choiceBoxTypeOfPipe);

        Label labelConcentration = new Label("Концентрация (мг/л)");
        labelConcentration.setId("textWithTooltip");
        Tooltip.install(labelConcentration, new Tooltip("Концентрация примести в стоках(мг/л)"));
        TextField textFieldConcentration = new TextField();
        textFieldConcentration.textProperty().addListener(new ValidationChangeListener(textFieldConcentration));
        content.put("textFieldConcentration", textFieldConcentration);
        VBox concentrationContainer = new VBox(labelConcentration, textFieldConcentration);




        Label labelWastewater = new Label("Расход ст. вод (м^3/сек)");
        labelWastewater.setId("textWithTooltip");
        Tooltip.install(labelWastewater, new Tooltip("Расход сточных вод (м^3/сек)"));
        TextField textFieldWastewater = new TextField();
        textFieldWastewater.textProperty().addListener(new ValidationChangeListener(textFieldWastewater));
        content.put("textFieldWastewater", textFieldWastewater);
        VBox wastewaterContainer = new VBox(labelWastewater, textFieldWastewater);


        Label timeLabel = new Label("Время работы (мин)");
        TextField textFieldTime = new TextField();
        textFieldTime.textProperty().addListener(new ValidationChangeListener(textFieldTime));
        VBox timeContainer = new VBox(timeLabel, textFieldTime);

        Label delayLabel = new Label("Время задержки (мин)");
        TextField textFieldPause = new TextField();
        textFieldPause.textProperty().addListener(new ValidationChangeListener(textFieldPause));
        VBox pauseContainer = new VBox(delayLabel, textFieldPause);




        Label modeLabel = new Label("Режим выпуска");
        ChoiceBox<String> choiceBoxModeOfPipe = new ChoiceBox(FXCollections.observableArrayList("Выключен", "Стационарный",
                "Периодический", "Однократного действия", "Залповый (Не работает)"));
        choiceBoxModeOfPipe.getSelectionModel().select("Стационарный");
        content.put("choiceBoxModeOfPipe", choiceBoxModeOfPipe);
        VBox modContainer = new VBox(modeLabel,choiceBoxModeOfPipe);
        choiceBoxModeOfPipe.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue)->{
            switch (oldValue){
                case "Периодический":
                    modContainer.getChildren().removeAll(timeContainer, pauseContainer);
                    content.keySet().remove("textFieldTime");
                    content.keySet().remove("textFieldPause");
                    break;
                case "Однократного действия":
                    modContainer.getChildren().remove(timeContainer);
                    content.keySet().remove("textFieldTime");
                    break;
                case "Залповый":
                    modContainer.getChildren().remove(timeContainer);
                    content.keySet().remove("textFieldTime");
                    break;
            }

            switch (newValue){
                case "Периодический":
                    modContainer.getChildren().addAll(timeContainer, pauseContainer);
                    content.put("textFieldTime", textFieldTime);
                    content.put("textFieldPause", textFieldPause);
                    break;
                case "Однократного действия":
                    modContainer.getChildren().add(timeContainer);
                    content.put("textFieldTime", textFieldTime);
                    break;
                case "Залповый":
                    flowPane.getChildren().add(timeContainer);
                    content.put("textFieldTime", textFieldTime);
                    break;
            }
        });



        Label bankLabel = new Label("Берег");
        bankLabel.setId("textWithTooltip");
        Tooltip.install(bankLabel, new Tooltip("Берег, с которого производится выпуск"));
        ChoiceBox<String> choiceBoxCoast = new ChoiceBox(FXCollections.observableArrayList("Левый", "Правый"));
        choiceBoxCoast.getSelectionModel().select(0);
        content.put("choiceBoxCoast", choiceBoxCoast);
        VBox bankContainer = new VBox(bankLabel, choiceBoxCoast);


        Label labelCoordinateX = new Label("Удаленность от К.С. (км)");
        labelCoordinateX.setId("textWithTooltip");
        Tooltip.install(labelCoordinateX, new Tooltip("Координата точки х - удаленность от контрольного створа"));
        TextField textFieldCoordinate = new TextField();
        textFieldCoordinate.textProperty().addListener(new ValidationChangeListener(textFieldCoordinate));

        content.put("textFieldCoordinate", textFieldCoordinate);
        VBox coordinateContainer = new VBox(labelCoordinateX, textFieldCoordinate);

        flowPane.getChildren().addAll(typeContainer, concentrationContainer, wastewaterContainer, modContainer, bankContainer, coordinateContainer);

        /*_______________________________________________________*/


        Label labelLength = new Label();
        labelLength.setId("textWithTooltip");
        TextField textFieldLength = new TextField();
        textFieldLength.textProperty().addListener(new ValidationChangeListener(textFieldLength));
        VBox lengthContainer = new VBox(labelLength, textFieldLength);


        Label labelSpacingPipes = new Label("Расст-ие м/д патрубками (м)");
        labelSpacingPipes.setId("textWithTooltip");
        Tooltip.install(labelSpacingPipes, new Tooltip("Расстояние между патрубками (м)"));
        TextField textFieldSpacingPipes = new TextField();
        textFieldSpacingPipes.textProperty().addListener(new ValidationChangeListener(textFieldSpacingPipes));
        VBox spacingPipesContainer = new VBox(labelSpacingPipes, textFieldSpacingPipes);



        Label labelDistanceToPipe = new Label("Расст-ие до 1 патрубка (м)");
        labelDistanceToPipe.setId("textWithTooltip");
        Tooltip.install(labelDistanceToPipe, new Tooltip("Расстояние от берега до 1 патрубка (м)"));
        TextField textFieldDistanceToPipe = new TextField();
        textFieldDistanceToPipe.textProperty().addListener(new ValidationChangeListener(textFieldDistanceToPipe));
        VBox distanceToPipeContainer = new VBox(labelDistanceToPipe, textFieldDistanceToPipe);

        Label labelSpacingBranch = new Label("Расст-ие м/д ветками (м)");
        labelSpacingBranch.setId("textWithTooltip");
        Tooltip.install(labelSpacingBranch, new Tooltip("Расстояние между ветками (м)"));
        TextField textFieldSpacingBranch = new TextField();
        textFieldSpacingBranch.textProperty().addListener(new ValidationChangeListener(textFieldSpacingBranch));


        VBox spacingBranchContainer = new VBox(labelSpacingBranch, textFieldSpacingBranch);

        /* ___________________________________________________________ */


        TitledPane titledPane = new TitledPane(null, root);

        choiceBoxTypeOfPipe.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            switch (oldValue) {
                case "Береговой распределенный":
                    content.keySet().remove("textFieldLength");
                    content.keySet().remove("textFieldSpacingPipes");
                    flowPane.getChildren().remove(lengthContainer);
                    flowPane.getChildren().remove(spacingPipesContainer);
                    break;
                case "Русловой сосредоточенный":
                    content.keySet().remove("textFieldLength");
                    flowPane.getChildren().remove(lengthContainer);
                    break;
                case "Русловой рассеивающий":
                    content.keySet().remove("textFieldLength");
                    content.keySet().remove("textFieldSpacingPipes");
                    content.keySet().remove("textFieldDistanceToPipe");
                    flowPane.getChildren().removeAll(lengthContainer, distanceToPipeContainer, spacingPipesContainer);
                    break;
                case "Русловой рассеивающий с 2 ветками":
                    content.keySet().remove("textFieldLength");
                    content.keySet().remove("textFieldSpacingPipes");
                    content.keySet().remove("textFieldDistanceToPipe");
                    content.keySet().remove("textFieldSpacingBranch");
                    flowPane.getChildren().removeAll(lengthContainer, distanceToPipeContainer, spacingPipesContainer, spacingBranchContainer);
                    break;
            }
            switch (newValue) {
                case "Береговой распределенный":
                    content.put("textFieldLength", textFieldLength);
                    content.put("textFieldSpacingPipes", textFieldSpacingPipes);
                    labelLength.setText("Распределенная часть (м)");
                    Tooltip.install(labelLength, new Tooltip("Длина распределенной части выпуска (м)"));
                    flowPane.getChildren().addAll(lengthContainer, spacingPipesContainer);
                    break;
                case "Русловой сосредоточенный":
                    labelLength.setText("Протяженность (м)");
                    content.put("textFieldLength", textFieldLength);
                    Tooltip.install(labelLength, new Tooltip("Расстояние от берега до точки выпуска"));
                    flowPane.getChildren().add(lengthContainer);
                    break;
                case "Русловой рассеивающий":
                    labelLength.setText("Рассеивающая часть (м)");
                    content.put("textFieldLength", textFieldLength);
                    content.put("textFieldSpacingPipes", textFieldSpacingPipes);
                    content.put("textFieldDistanceToPipe", textFieldDistanceToPipe);
                    Tooltip.install(labelLength, new Tooltip("Длина рассеивающей части"));
                    flowPane.getChildren().add(lengthContainer);
                    flowPane.getChildren().addAll(distanceToPipeContainer, spacingPipesContainer);
                    break;
                case "Русловой рассеивающий с 2 ветками":
                    labelLength.setText("Рассеивающая часть (м)");
                    content.put("textFieldLength", textFieldLength);
                    content.put("textFieldSpacingPipes", textFieldSpacingPipes);
                    content.put("textFieldDistanceToPipe", textFieldDistanceToPipe);
                    content.put("textFieldSpacingBranch", textFieldSpacingBranch);
                    Tooltip.install(labelLength, new Tooltip("Длина рассеивающей части"));
                    flowPane.getChildren().add(lengthContainer);
                    flowPane.getChildren().addAll(distanceToPipeContainer, spacingPipesContainer, spacingBranchContainer);
                    break;
            }
            /*First solution*/
            //flowPane.autosize();
            //titledPane.autosize();

            /*Second solution*/
            //flowPane.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);

            /*Third solution*/
            //flowPane.setPrefWrapLength(Region.USE_COMPUTED_SIZE);
            //flowPane.prefHeightProperty().bind(titledPane.heightProperty());
            //titledPane.autosize();


        });

        BorderPane borderPane = new BorderPane();
        Label titleOfTitledPane = new Label("Параметры выпуска " + index);
        Button buttonClose = new Button("X");
        Tooltip.install(buttonClose, new Tooltip("Удалить выпуск"));
        buttonClose.setOnAction((value)->{
            this.accordionPlant.getPanes().remove(titledPane);
            this.dynamicComponents.remove(content);
            int counter = 1;
            for(TitledPane pane : this.accordionPlant.getPanes()){
                BorderPane border = (BorderPane) pane.getGraphic();
                Label label = (Label) border.getCenter();
                label.setText("Параметры выпуска " + counter++);
            }
        });
        borderPane.setCenter(titleOfTitledPane);
        borderPane.setRight(buttonClose);
        borderPane.prefWidthProperty().bind(this.textFieldConcentration.getScene().widthProperty().subtract(40));
        titledPane.setGraphic(borderPane);

        this.dynamicComponents.add(content);
        return titledPane;
    }

    /**
     * Создает массив труб, используя данные из графического интерефейса.<br>
     * В массив входят только работающие трубы
     * @return массив труб
     */
    private BasePipe[] createPipesFromGUI() throws NoPipesException {
        //Подсчитаем количетво не выключенных выпусков
        int counter = 0;
        for(Map<String, Node> titledPane : dynamicComponents){
            ChoiceBox<String> mode = (ChoiceBox<String>) titledPane.get("choiceBoxModeOfPipe");
           if( !mode.getValue().equals("Выключен") ) counter++;
        }
        if(counter == 0) throw new NoPipesException();
        //Выделим под невыключенные выпуска память
        BasePipe[] pipes = new BasePipe[counter];

        //Начнем инициализировать поля труб



        counter = 0;
        Mode mode = null;
        for(Map<String, Node> titledPane : dynamicComponents){
            ChoiceBox<String> modeChoiceBox = (ChoiceBox<String>) titledPane.get("choiceBoxModeOfPipe");
            if( !modeChoiceBox.getValue().equals("Выключен") ) {

                switch (modeChoiceBox.getValue()){
                    case "Стационарный": {
                        mode = new StationaryMode();
                        break;
                    }
                    case "Периодический": {
                        TextField textFieldTime = (TextField) titledPane.get("textFieldTime");
                        TextField textFieldPause = (TextField) titledPane.get("textFieldPause");
                        mode = new PeriodicalMode(Integer.valueOf(textFieldTime.getText()), Integer.valueOf(textFieldPause.getText()));
                        break;
                    }
                    case "Однократного действия": {
                        TextField textFieldTime = (TextField) titledPane.get("textFieldTime");
                        mode = new SingleEntryMode(Integer.valueOf(textFieldTime.getText()));
                        break;
                    }
                    case "Залповый": {
                        TextField textFieldTime = (TextField) titledPane.get("textFieldTime");
                        mode = new VolleyMode(Integer.valueOf(textFieldTime.getText()));
                        break;
                    }
                }


                double x = Double.parseDouble(( (TextField) titledPane.get("textFieldCoordinate")).getText());
                boolean coast = false;
                String temp = ((ChoiceBox<String>) titledPane.get("choiceBoxCoast")).getValue();
                if(temp.equals("Левый")) coast = true;
                double concentration = Double.parseDouble(( (TextField) titledPane.get("textFieldConcentration")).getText());
                double wastewater = Double.parseDouble(( (TextField) titledPane.get("textFieldWastewater")).getText());
                ChoiceBox<String> typeChoiceBox = (ChoiceBox<String>) titledPane.get("choiceBoxTypeOfPipe");


                switch (typeChoiceBox.getValue()){

                    case "Береговой сосредоточенный": {
                        pipes[counter++] = new CoastalConcentratedPipe(x, coast, mode, concentration, wastewater);
                        break;
                    }
                    case "Береговой распределенный": {
                        double length = Double.parseDouble(( (TextField) titledPane.get("textFieldLength")).getText());
                        double spacingPipes = Double.parseDouble(( (TextField) titledPane.get("textFieldSpacingPipes")).getText());
                        pipes[counter++] = new CoastalSpreadPipe(x, coast, mode, length, spacingPipes, concentration, wastewater);
                        break;
                    }
                    case "Русловой сосредоточенный": {
                        double length = Double.parseDouble(( (TextField) titledPane.get("textFieldLength")).getText());
                        pipes[counter++] = new ChanneledConcentratedPipe(x, coast, mode, length, concentration, wastewater);
                        break;
                    }
                    case "Русловой рассеивающий": {
                        double length = Double.parseDouble(( (TextField) titledPane.get("textFieldLength")).getText());
                        double spacingPipes = Double.parseDouble(( (TextField) titledPane.get("textFieldSpacingPipes")).getText());
                        double distanceToPipe = Double.parseDouble(( (TextField) titledPane.get("textFieldDistanceToPipe")).getText());
                        pipes[counter++] = new ChanneledSpreadPipe(x, coast, mode, length, spacingPipes, distanceToPipe, concentration, wastewater);
                        break;
                    }
                    /*case "Русловой рассеивающий с 2 ветками": {
                        double length = Double.parseDouble(( (TextField) titledPane.get("textFieldLength")).getText());
                        double spacingPipes = Double.parseDouble(( (TextField) titledPane.get("textFieldSpacingPipes")).getText());
                        double distanceToPipe = Double.parseDouble(( (TextField) titledPane.get("textFieldDistanceToPipe")).getText());
                        double spacingBranch = Double.parseDouble(( (TextField) titledPane.get("textFieldSpacingBranch")).getText());
                        //pipes[counter++] = new Sprea
                        break;
                    }*/
                }
            }
        }
        return pipes;
    }

    /**
     * Добавляет в {@link #accordionPlant} форму для введения данных о трубе
     */
    @FXML
    private void addTitledPanePipe() {
        int size = this.accordionPlant.getPanes().size();
        this.accordionPlant.getPanes().add(createTitledPanePipe(size+1));
        this.accordionPlant.getPanes().get(size).setExpanded(true);
    }


    /**
     * Запускаем калькулятор поперечной диффузии.
     * Такие параметры как: ширина реки, глубина реки и скорость течения реки,
     * будут отправлены в калькулятор, если они проинициализированы пользователем
     * в соответствующих полях главного окна.
     */
    public void runDiffusionCalculator() {
        /*//Если в окно калькулятора придут отрицательные значения, значит поля не были заполнены пользователем
        double riverWidth = -1;
        double riverDepth = -1;
        double riverFlowSpeed = -1;
        //_____________________________________________________________________________________________________
        String temp = this.textFieldRiverWidth.getText();
        if(!temp.isEmpty()){ riverWidth = Double.parseDouble(temp); }
        temp = .getText();
        if(!temp.isEmpty()){ riverDepth = Double.parseDouble(temp); }
        temp = .getText();
        if(!temp.isEmpty()) { riverFlowSpeed = Double.parseDouble(temp); }

        River riverInfo = new River(riverWidth, riverDepth, riverFlowSpeed);*/
        ApplicationFactory.getInstance().getDiffusionApp(this.textFieldConcentration.getScene(), this.textFieldRiverWidth.textProperty(), this.textFieldRiverDepth.textProperty(), this.textFieldFlowSpeed.textProperty(), this.textFieldDiffusionCoef.textProperty()).show();
    }

    public void calculate(ActionEvent event) {

//        BasePipe[] pipes = new BasePipe[3];
//
//        pipes[0] = new CoastalConcentratedPipe(24.5, false, new StationaryMode(), 6.88, 0.10);
//        pipes[1] = new CoastalConcentratedPipe(5.5, true, new StationaryMode(), 4.05, 0.03);
//        pipes[2] = new CoastalConcentratedPipe(0.5, false, new StationaryMode(), 5.54, 0.31);
//        //pipes[0] = new ChanneledConcentratedPipe(0.5, false, new PeriodicalMode(1,1),33 , 5.54, 0.31);
//        Model model = new Model(new River(110.0, 1.0, 0.04, 0.12, 3.0, 0.0) ,pipes, new Substance("Нефтепродукты", 0.5 , 0.05),4, 280*60, 60);
//        ApplicationFactory.getInstance().getResultWindow(this.textFieldConcentration.getScene(), model).show();



        Model model;
        BasePipe[] pipes;
        River riverInfo;
        Substance substance;
        double dt = 0;
        double endTime = 0;
        double splitDt = 0;
        try {
            riverInfo = this.createRiverFromGUI();
            substance = this.createSubstanceFromGUI();
            pipes = this.createPipesFromGUI();
            Arrays.sort(pipes);
            TimeChooserWindow timeChooserWindow = ApplicationFactory.getInstance().getTimeChooserWindow(this.textFieldConcentration.getScene(), pipes[pipes.length-1].getEndX(), riverInfo.flowSpeed);
            timeChooserWindow.show();
            if(timeChooserWindow.wasClosed()) return;
            dt = timeChooserWindow.getDt();
            endTime = timeChooserWindow.getEndTime();
            splitDt = timeChooserWindow.getSpliceDt();
        } catch (NumberFormatException ex){
            ApplicationFactory.getInstance().getAlert("Все поля должны быть заполнены!").showAndWait();
            return;
        } catch (NoPipesException e) {
            ApplicationFactory.getInstance().getAlert("Нет работающих труб!").showAndWait();
            return;
        }
        model = new Model(riverInfo, pipes, substance,Integer.parseInt(textFieldRound.getText()), endTime*60, dt, splitDt);
        ApplicationFactory.getInstance().getResultWindow(textFieldRound.getScene(), model).show();


    }


    public void init(){
         this.textFieldNonConservatismCoef.textProperty().addListener(new ValidationChangeListener(textFieldNonConservatismCoef));
         this.textFieldDiffusionCoef.textProperty().addListener(new ValidationChangeListener(textFieldDiffusionCoef));
         this.textFieldFlowSpeed.textProperty().addListener(new ValidationChangeListener(textFieldFlowSpeed));
         this.textFieldRiverDepth.textProperty().addListener(new ValidationChangeListener(textFieldRiverDepth));
         this.textFieldRiverWidth.textProperty().addListener(new ValidationChangeListener(textFieldRiverWidth));
         this.textFieldProportion.textProperty().addListener(new ValidationChangeListener(textFieldProportion));
         this.textFieldLAC.textProperty().addListener(new ValidationChangeListener(textFieldLAC));
         this.textFieldConcentration.textProperty().addListener(new ValidationChangeListener(textFieldConcentration));
         this.textFieldRound.textProperty().addListener(new ValidationChangeListener(textFieldRound));
    }





    private River createRiverFromGUI(){
        double width = Double.parseDouble(this.textFieldRiverWidth.getText());
        double depth = Double.parseDouble(this.textFieldRiverDepth.getText());
        double backgroundConc = Double.parseDouble(this.textFieldConcentration.getText());
        double diffusy = Double.parseDouble(this.textFieldDiffusionCoef.getText());
        double flowSpeed = Double.parseDouble(this.textFieldFlowSpeed.getText());
        double nonCons = Double.parseDouble(this.textFieldNonConservatismCoef.getText());
        
        return new River(width, depth , backgroundConc, diffusy, flowSpeed, nonCons);
    }

    private Substance createSubstanceFromGUI() {

        if(textFieldSubstance.getText().isEmpty()) throw new NumberFormatException();
        Substance substance = new Substance( this.textFieldSubstance.getText(),
                Double.parseDouble(this.textFieldProportion.getText()),
                    Double.parseDouble(this.textFieldLAC.getText()));
        return substance;
    }

    @FXML
    void saveData(){
        File directory = new File("input");
        directory.mkdir();

        FileChooser fileChooser = new FileChooser();//Класс работы с диалогом выборки и сохранения
        fileChooser.setTitle("Save Document");//Заголовок диалога
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("Water Project (*.wtr)", "*.wtr");//Расширение
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setInitialDirectory(directory.getAbsoluteFile());
        File file = fileChooser.showSaveDialog(this.textFieldConcentration.getScene().getWindow());//Указываем текущую сцену
        if (file != null) {
            FileWriter writer = null;
            try {
                writer = new FileWriter(file.getAbsolutePath(), false);
                writer.write(this.textFieldRiverWidth.getText() + "\n");
                writer.write(this.textFieldRiverDepth.getText() + "\n");
                writer.write(this.textFieldFlowSpeed.getText() + "\n");
                writer.write(this.textFieldDiffusionCoef.getText() + "\n");
                writer.write(this.textFieldNonConservatismCoef.getText() + "\n");
                writer.write(this.textFieldSubstance.getText() + "\n");
                writer.write(this.textFieldConcentration.getText() + "\n");
                writer.write(this.textFieldProportion.getText() + "\n");
                writer.write(this.textFieldLAC.getText() + "\n");
                writer.write(this.dynamicComponents.size() + "\n");
                for(Map<String, Node> title : this.dynamicComponents){
                    for(Node comp : title.values()){
                        if(comp instanceof ChoiceBox){
                            writer.write( ((ChoiceBox) comp).getValue().toString() + "\n");
                        }
                    }
                    for(Node comp : title.values()){
                        if(comp instanceof TextField ){
                            writer.write( ((TextField) comp).getText() + "\n");
                        }
                    }

                }

                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    @FXML
    void loadData(){
        File input = new File("input");
        input.mkdir();
        FileChooser fileChooser = new FileChooser();//Класс работы с диалогом выборки и сохранения
        fileChooser.setTitle("Open Document");//Заголовок диалога
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("Water Project (*.wtr)", "*.wtr");//Расширение
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setInitialDirectory(input.getAbsoluteFile());
        File file = fileChooser.showOpenDialog(this.textFieldConcentration.getScene().getWindow());//Указываем текущую сцену CodeNote.mainStage
        if (file != null) {
                String[] parametres = null;
                try {
                   parametres = (String[]) Files.lines(Paths.get(file.getAbsolutePath()), StandardCharsets.UTF_8).toArray(String[]::new);

                    int n = this.accordionPlant.getPanes().size();
                    for (int i = 0; i < n; i++) {
                        this.accordionPlant.getPanes().remove(0);
                    }


                    this.dynamicComponents = new LinkedList<Map<String, Node>>();

                    int i = 0;
                    this.textFieldRiverWidth.setText(parametres[i++]);
                    this.textFieldRiverDepth.setText(parametres[i++]);
                    this.textFieldFlowSpeed.setText(parametres[i++]);
                    this.textFieldDiffusionCoef.setText(parametres[i++]);
                    this.textFieldNonConservatismCoef.setText(parametres[i++]);


                    this.textFieldSubstance.setText(parametres[i++]);
                    this.textFieldConcentration.setText(parametres[i++]);
                    this.textFieldProportion.setText(parametres[i++]);
                    this.textFieldLAC.setText(parametres[i++]);

                    int temp = Integer.parseInt(parametres[i++]);

                    for (int j = 0; j < temp; j++) {
                        addTitledPanePipe();
                        Map<String, Node> title = this.dynamicComponents.get(j);
                        ((ChoiceBox<String>)title.get("choiceBoxTypeOfPipe")).setValue(parametres[i++]);
                        ((ChoiceBox<String>)title.get("choiceBoxCoast")).setValue(parametres[i++]);
                        ((ChoiceBox<String>)title.get("choiceBoxModeOfPipe")).setValue(parametres[i++]);
                        for(Node comp : title.values()){
                            if(comp instanceof TextField ){
                                ((TextField) comp).setText(parametres[i++]);
                            }
                        }



                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    ApplicationFactory.getInstance().getAlert("Не удалось загрузить файл!").showAndWait();
                }
        }

    }

    @FXML
    void cleanFields(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Новые данные");
        alert.setHeaderText("Вы уверены?");
        alert.setContentText("Все несохраненные данные будут утеряны");
        // option != null.
        Optional<ButtonType> option = alert.showAndWait();

        if (option.get() == ButtonType.OK) {
            int n = this.accordionPlant.getPanes().size();
            for (int i = 0; i < n; i++) {
                this.accordionPlant.getPanes().remove(0);
            }
            this.dynamicComponents = new LinkedList<Map<String, Node>>();

            this.textFieldRiverWidth.setText("");
            this.textFieldRiverDepth.setText("");
            this.textFieldDiffusionCoef.setText("");
            this.textFieldFlowSpeed.setText("");
            this.textFieldNonConservatismCoef.setText("");


            this.textFieldSubstance.setText("");
            this.textFieldConcentration.setText("");
            this.textFieldProportion.setText("");
            this.textFieldLAC.setText("");
        }
    }

    @FXML
    void exitProgram(){
        ((Stage)(this.textFieldConcentration.getScene().getWindow())).close();
    }

    public void showAbout(){
        ApplicationFactory.getInstance().getAboutWindow(this.textFieldConcentration.getScene()).show();
    }

}
