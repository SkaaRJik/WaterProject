package application;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import model.pipe.BasePipe;

import java.util.ArrayList;

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





    public void fillAccordion(int numberOfPipes){

        accordionPlant.getPanes().add(createTitledPaneCoastalConcentratedPipe(0));
        accordionPlant.getPanes().add(createTitledPaneCoastalSpreadPipe(1));

    }

    /**
     *
     * @param index
     * @return
     */
    protected TitledPane createTitledPaneCoastalConcentratedPipe(int index){

        TilePane tile = new TilePane(Orientation.HORIZONTAL, 5, 5);
        Label typeLabel = new Label("Тип выпуска");
        ChoiceBox<String> choiceBoxTypeOfPipe = createChoiceBoxTypeOfPipes();
        choiceBoxTypeOfPipe.getSelectionModel().select(0);
        choiceBoxTypeOfPipe.setPrefWidth(choiceBoxTypeOfPipe.USE_COMPUTED_SIZE);
        choiceBoxTypeOfPipe.setPrefHeight(choiceBoxTypeOfPipe.USE_COMPUTED_SIZE);
        VBox typeContainer = new VBox(typeLabel,choiceBoxTypeOfPipe);

        Label bankLabel = new Label("Берег");
        bankLabel.setId("textWithTooltip");
        Tooltip.install(bankLabel, new Tooltip("Берег, с которого производится выпуск"));
        ChoiceBox<String> choiceBoxCoast = createChoiceBoxCoast();
        choiceBoxCoast.getSelectionModel().select(0);
        VBox bankContainer = new VBox(bankLabel, choiceBoxCoast);

        Label labelCoordinateX = new Label("Точка выпуска (км)");
        Tooltip.install(labelCoordinateX, new Tooltip("Координата x точки выпуска относительно положения контрольного створа"));
        labelCoordinateX.setId("textWithTooltip");

        TextField textFieldCoordinate = new TextField();

        VBox coordinateContainer = new VBox(labelCoordinateX, textFieldCoordinate);

        tile.getChildren().addAll(typeContainer, bankContainer, coordinateContainer);

        TitledPane titledPane = new TitledPane("Параметры выпуска " + (index+1), tile);

        return titledPane;
    }

    protected TitledPane createTitledPaneCoastalSpreadPipe(int index){

        TilePane tile = new TilePane(Orientation.HORIZONTAL, 5, 5);
        Label typeLabel = new Label("Тип выпуска");
        ChoiceBox<String> choiceBoxTypeOfPipe = createChoiceBoxTypeOfPipes();
        choiceBoxTypeOfPipe.getSelectionModel().select(1);
        /*choiceBoxTypeOfPipe.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(!oldValue.equals(newValue)) {
                switch (newValue) {
                    case "Береговой сосредоточенный":
                }
            }
        });*/
        VBox typeContainer = new VBox(typeLabel,choiceBoxTypeOfPipe);

        Label bankLabel = new Label("Берег");
        bankLabel.setId("textWithTooltip");
        Tooltip.install(bankLabel, new Tooltip("Берег, с которого производится выпуск"));
        ChoiceBox<String> choiceBoxCoast = createChoiceBoxCoast();
        choiceBoxCoast.getSelectionModel().select(0);
        VBox bankContainer = new VBox(bankLabel, choiceBoxCoast);

        Label labelCoordinateX = new Label("Точка выпуска (км)");
        labelCoordinateX.setId("textWithTooltip");
        Tooltip.install(labelCoordinateX, new Tooltip("Координата x точки выпуска относительно положения контрольного створа"));

        TextField textFieldCoordinate = new TextField();
        VBox coordinateContainer = new VBox(labelCoordinateX, textFieldCoordinate);

        Label labelLength = new Label("Распределенная часть (м)");
        labelLength.setId("textWithTooltip");
        Tooltip.install(labelLength, new Tooltip("Длина распределенной части выпуска (м)"));
        TextField textFieldLength = new TextField();
        VBox lengthContainer = new VBox(labelLength, textFieldLength);

        tile.getChildren().addAll(typeContainer, bankContainer, coordinateContainer, lengthContainer);

        TitledPane titledPane = new TitledPane("Параметры выпуска " + (index+1), tile);


        return titledPane;
    }


    protected ChoiceBox<String> createChoiceBoxTypeOfPipes(){
        return new ChoiceBox(FXCollections.observableArrayList("Береговой сосредоточенный",
                "Береговой распределенный", "Русловой сосредоточенный", "Русловой рассеивающий",
                "Русловой рассеивающий с двумя ветками выпуска"));

    }

    protected ChoiceBox<String> createChoiceBoxCoast(){
        return new ChoiceBox(FXCollections.observableArrayList("Левый", "Правый"));
    }



    private BasePipe[] createPipesFromGUI(){
        return new BasePipe[1];
    }


}
