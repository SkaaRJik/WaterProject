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


        Label labelCoordinateX = new Label("Точка выпуска (км)");
        labelCoordinateX.setId("textWithTooltip");
        Tooltip.install(labelCoordinateX, new Tooltip("Координата точки х - удаленность от контрольного створа"));
        TextField textFieldCoordinate = new TextField();
        content.put("textFieldCoordinate", textFieldCoordinate);
        VBox coordinateContainer = new VBox(labelCoordinateX, textFieldCoordinate);


        flowPane.getChildren().addAll(typeContainer, modContainer, bankContainer, coordinateContainer);

        /*_______________________________________________________*/

        /* Береговая распределенная труба */

        Label labelLength = new Label();
        labelLength.setId("textWithTooltip");
        TextField textFieldLength = new TextField();
        VBox lengthContainer = new VBox(labelLength, textFieldLength);

        Label labelMiniPipes = new Label("Патрубки (м)");
        labelMiniPipes.setId("textWithTooltip");
        Tooltip.install(labelMiniPipes, new Tooltip("Расстояние между патрубками (м)"));
        TextField textFieldMiniPipes = new TextField();
        VBox miniPipesContainer = new VBox(labelMiniPipes, textFieldMiniPipes);

        /* ___________________________________________________________ */


        TitledPane titledPane = new TitledPane(null, flowPane);

        choiceBoxTypeOfPipe.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            switch (oldValue) {
                case "Береговой распределенный":
                    content.keySet().remove("textFieldLength");
                    content.keySet().remove("textFieldMiniPipes");
                    flowPane.getChildren().remove(lengthContainer);
                    flowPane.getChildren().remove(miniPipesContainer);
                    break;
                case "Русловой сосредоточенный":
                    content.keySet().remove("textFieldLength");
                    flowPane.getChildren().remove(lengthContainer);
                    break;

            }
            switch (newValue) {
                case "Береговой распределенный":
                    content.put("textFieldLength", textFieldLength);
                    content.put("textFieldMiniPipes", textFieldMiniPipes);
                    labelLength.setText("Распределенная часть (м)");
                    Tooltip.install(labelLength, new Tooltip("Длина распределенной части выпуска (м)"));
                    flowPane.getChildren().addAll(lengthContainer, miniPipesContainer);
                    break;
                case "Русловой сосредоточенный":
                    labelLength.setText("Протяженность (м)");
                    content.put("textFieldLength", textFieldLength);
                    Tooltip.install(labelLength, new Tooltip("Расстояние от берега до точки выпуска"));
                    flowPane.getChildren().add(lengthContainer);
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
        return new BasePipe[1];
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
}
