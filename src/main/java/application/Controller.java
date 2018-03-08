package application;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import model.pipe.BasePipe;
import model.pipe.CoastalConcentratedPipe;
import model.pipe.CoastalSpreadPipe;
import model.pipe.mode.*;
import model.river.River;

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
    TextField textFieldNumberOfPipes;
    @FXML
    Accordion accordionPlant;

    private Scene scene;

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
        Tooltip.install(labelConcentration, new Tooltip("Расстояние между ветками (м)"));
        TextField textFieldConcentration = new TextField();
        content.put("textFieldConcentration", textFieldConcentration);
        VBox concentrationContainer = new VBox(labelConcentration, textFieldConcentration);




        Label labelWastewater = new Label("Расход ст. вод (м^3/сек)");
        labelWastewater.setId("textWithTooltip");
        Tooltip.install(labelWastewater, new Tooltip("Расход сточных вод (м^3/сек)"));
        TextField textFieldWastewater = new TextField();
        content.put("textFieldWastewater", textFieldWastewater);
        VBox wastewaterContainer = new VBox(labelWastewater, textFieldWastewater);


        Label timeLabel = new Label("Время работы (с)");
        TextField textFieldTime = new TextField();
        VBox timeContainer = new VBox(timeLabel, textFieldTime);

        Label pauseLabel = new Label("Время задержки (с)");
        TextField textFieldPause = new TextField();
        VBox pauseContainer = new VBox(pauseLabel, textFieldPause);




        Label modeLabel = new Label("Режим выпуска");
        ChoiceBox<String> choiceBoxModeOfPipe = new ChoiceBox(FXCollections.observableArrayList("Выключен", "Стационарный",
                "Периодический", "Однократного действия", "Залповый"));
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


        Label labelCoordinateX = new Label("Точка начала  выпуска (км)");
        labelCoordinateX.setId("textWithTooltip");
        Tooltip.install(labelCoordinateX, new Tooltip("Координата точки х - удаленность от контрольного створа"));
        TextField textFieldCoordinate = new TextField();
        content.put("textFieldCoordinate", textFieldCoordinate);
        VBox coordinateContainer = new VBox(labelCoordinateX, textFieldCoordinate);

        flowPane.getChildren().addAll(typeContainer, concentrationContainer, wastewaterContainer, modContainer, bankContainer, coordinateContainer);

        /*_______________________________________________________*/


        Label labelLength = new Label();
        labelLength.setId("textWithTooltip");
        TextField textFieldLength = new TextField();
        VBox lengthContainer = new VBox(labelLength, textFieldLength);


        Label labelSpacingPipes = new Label("Расст-ие м/д патрубками (м)");
        labelSpacingPipes.setId("textWithTooltip");
        Tooltip.install(labelSpacingPipes, new Tooltip("Расстояние между патрубками (м)"));
        TextField textFieldSpacingPipes = new TextField();
        VBox spacingPipesContainer = new VBox(labelSpacingPipes, textFieldSpacingPipes);



        Label labelDistanceToPipe = new Label("Расст-ие до 1 патрубка (м)");
        labelDistanceToPipe.setId("textWithTooltip");
        Tooltip.install(labelDistanceToPipe, new Tooltip("Расстояние от берега до 1 патрубка (м)"));
        TextField textFieldDistanceToPipe = new TextField();
        VBox distanceToPipeContainer = new VBox(labelDistanceToPipe, textFieldDistanceToPipe);

        Label labelSpacingBranch = new Label("Расст-ие м/д ветками (м)");
        labelSpacingBranch.setId("textWithTooltip");
        Tooltip.install(labelSpacingBranch, new Tooltip("Расстояние между ветками (м)"));
        TextField textFieldSpacingBranch = new TextField();
        VBox spacingBranchContainer = new VBox(labelSpacingBranch, textFieldSpacingBranch);

        /* ___________________________________________________________ */


        TitledPane titledPane = new TitledPane(null, flowPane);

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
        borderPane.prefWidthProperty().bind(this.scene.widthProperty().subtract(40));
        titledPane.setGraphic(borderPane);

        this.dynamicComponents.add(content);
        return titledPane;
    }


    private BasePipe[] createPipesFromGUI(){
        //Подсчитаем количетво не выключенных выпусков
        int counter = 0;
        for(Map<String, Node> titledPane : dynamicComponents){
            ChoiceBox<String> mode = (ChoiceBox<String>) titledPane.get("choiceBoxModeOfPipe");
           if( !mode.getValue().equals("Выключен") ) counter++;
        }
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



            }






            ChoiceBox<String> typeChoiceBox = (ChoiceBox<String>) titledPane.get("choiceBoxTypeOfPipe");
            switch (typeChoiceBox.getValue()){
                case "Береговой сосредоточенный": {
                    double x = Double.parseDouble(( (TextField) titledPane.get("textFieldCoordinate")).getText());
                    boolean coast = false;
                    String temp = ((ChoiceBox<String>) titledPane.get("choiceBoxCoast")).getValue();
                    if(temp.equals("Левый")) coast = true;
                    double concentration = Double.parseDouble(( (TextField) titledPane.get("textFieldConcentration")).getText());
                    double wastewater = Double.parseDouble(( (TextField) titledPane.get("textFieldWastewater")).getText());
                    pipes[counter++] = new CoastalConcentratedPipe(x, coast, mode, concentration, wastewater);
                    break;
                }
                case "Береговой распределенный ": {
                    double x = Double.parseDouble(( (TextField) titledPane.get("textFieldCoordinate")).getText());
                    double length = Double.parseDouble(( (TextField) titledPane.get("textFieldLength")).getText());
                    boolean coast = false;
                    String temp = ((ChoiceBox<String>) titledPane.get("choiceBoxCoast")).getValue();
                    if(temp.equals("Левый")) coast = true;
                    double concentration = Double.parseDouble(( (TextField) titledPane.get("textFieldConcentration")).getText());
                    double wastewater = Double.parseDouble(( (TextField) titledPane.get("textFieldWastewater")).getText());
                    double spacingPipes = Double.parseDouble(( (TextField) titledPane.get("textFieldSpacingPipes")).getText());
                    pipes[counter++] = new CoastalSpreadPipe(x, coast, mode, length, spacingPipes, concentration, wastewater);
                    break;
                }
                case "Русловой сосредоточенный": {
                    double x = Double.parseDouble(( (TextField) titledPane.get("textFieldCoordinate")).getText());
                    double length = Double.parseDouble(( (TextField) titledPane.get("textFieldLength")).getText());
                    boolean coast = false;
                    String temp = ((ChoiceBox<String>) titledPane.get("choiceBoxCoast")).getValue();
                    if(temp.equals("Левый")) coast = true;
                    double concentration = Double.parseDouble(( (TextField) titledPane.get("textFieldConcentration")).getText());
                    double wastewater = Double.parseDouble(( (TextField) titledPane.get("textFieldWastewater")).getText());
                    double spacingPipes = Double.parseDouble(( (TextField) titledPane.get("textFieldSpacingPipes")).getText());
                    double distanceToPipe = Double.parseDouble(( (TextField) titledPane.get("textFieldDistanceToPipe")).getText());
                    double spacingBranch = Double.parseDouble(( (TextField) titledPane.get("textFieldSpacingBranch")).getText());
                    //pipes[counter++] = new
                    break;
                }
                case "Русловой рассеивающий": {
                    double x = Double.parseDouble(( (TextField) titledPane.get("textFieldCoordinate")).getText());
                    double length = Double.parseDouble(( (TextField) titledPane.get("textFieldLength")).getText());
                    boolean coast = false;
                    String temp = ((ChoiceBox<String>) titledPane.get("choiceBoxCoast")).getValue();
                    if(temp.equals("Левый")) coast = true;
                    double concentration = Double.parseDouble(( (TextField) titledPane.get("textFieldConcentration")).getText());
                    double wastewater = Double.parseDouble(( (TextField) titledPane.get("textFieldWastewater")).getText());
                    double spacingPipes = Double.parseDouble(( (TextField) titledPane.get("textFieldSpacingPipes")).getText());
                    double distanceToPipe = Double.parseDouble(( (TextField) titledPane.get("textFieldDistanceToPipe")).getText());
                    double spacingBranch = Double.parseDouble(( (TextField) titledPane.get("textFieldSpacingBranch")).getText());
                    //pipes[counter++] = new
                    break;
                }
                case "Русловой рассеивающий с 2 ветками": {

                    double x = Double.parseDouble(( (TextField) titledPane.get("textFieldCoordinate")).getText());
                    double length = Double.parseDouble(( (TextField) titledPane.get("textFieldLength")).getText());
                    boolean coast = false;
                    String temp = ((ChoiceBox<String>) titledPane.get("choiceBoxCoast")).getValue();
                    if(temp.equals("Левый")) coast = true;
                    double concentration = Double.parseDouble(( (TextField) titledPane.get("textFieldConcentration")).getText());
                    double wastewater = Double.parseDouble(( (TextField) titledPane.get("textFieldWastewater")).getText());
                    double spacingPipes = Double.parseDouble(( (TextField) titledPane.get("textFieldSpacingPipes")).getText());
                    double distanceToPipe = Double.parseDouble(( (TextField) titledPane.get("textFieldDistanceToPipe")).getText());
                    double spacingBranch = Double.parseDouble(( (TextField) titledPane.get("textFieldSpacingBranch")).getText());
                    //pipes[counter++] = new
                    break;
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

    public void setScene(Scene scene) {
        this.scene = scene;
    }


    /**
     * Запускаем калькулятор поперечной диффузии.
     * Такие параметры как: ширина реки, глубина реки и скорость течения реки,
     * будут отправлены в калькулятор, если они проинициализированы пользователем
     * в соответствующих полях главного окна.
     */
    public void runDiffusionCalculator() {
        //Если в окно калькулятора придут отрицательные значения, значит поля не были заполненны пользователем
        double riverWidth = -1;
        double riverDepth = -1;
        double riverFlowSpeed = -1;
        //_____________________________________________________________________________________________________
        String temp = this.textFieldRiverWidth.getText();
        if(!temp.isEmpty()){ riverWidth = Double.parseDouble(temp); }
        temp = this.textFieldRiverDepth.getText();
        if(!temp.isEmpty()){ riverDepth = Double.parseDouble(temp); }
        temp = this.textFieldFlowSpeed.getText();
        if(!temp.isEmpty()) { riverFlowSpeed = Double.parseDouble(temp); }

        River riverInfo = new River(riverWidth, riverDepth, riverFlowSpeed);
        ApplicationFactory.getInstance().getDiffusionApp(this.scene, riverInfo).show();
    }

    public void calculate(ActionEvent event) {
        createPipesFromGUI();
    }
}
