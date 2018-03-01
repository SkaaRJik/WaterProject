package application;

import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

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
    Accordion accordionPlant;


    public void init(){

        accordionPlant.getPanes().add(createTitledPanePlant(1));
        accordionPlant.getPanes().add(createTitledPanePlant(2));
    }

    /**
     *
     * @param index
     * @return
     */
    protected TitledPane createTitledPanePlant(int index){

        TilePane tile = new TilePane(Orientation.HORIZONTAL, 5, 5);
        Label typeLabel = new Label("Тип выпуска");
        TextField typeText = new TextField();
        VBox typeContainer = new VBox(typeLabel,typeText);

        Label bankLabel = new Label("Берег");
        Tooltip.install(bankLabel, new Tooltip("Берег, с которого производится выпуск"));
        TextField bankText = new TextField();
        VBox bankContainer = new VBox(bankLabel,bankText);

        tile.getChildren().addAll(typeContainer, bankContainer);

        TitledPane titledPane = new TitledPane("Параметры выпуска " + index, tile);

        return titledPane;
    }

    void test(){
        for (TitledPane pane: accordionPlant.getPanes()) {
            pane.getChildrenUnmodifiable().get(0); //blah, blah
        }
    }


}
