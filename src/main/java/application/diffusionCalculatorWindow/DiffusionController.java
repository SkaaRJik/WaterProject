package application.diffusionCalculatorWindow;

import application.ApplicationFactory;
import application.ValidationChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import model.river.River;
import utils.RiverMath;
import utils.Validator;

import java.util.HashMap;
import java.util.Map;

/**
 * Контроллер калькулятора поперечной диффузиии
 * Отвечает за логику интерфейса
 */
public class DiffusionController {

    @FXML
    FlowPane flowPane;
    @FXML
    ChoiceBox<String> choiceBoxRiver;
    @FXML
    TextField textFieldResult;
    @FXML
    TextField textFieldRiverDepth;
    Map<String, Node> componentsOfGUI = new HashMap<String, Node>();


    River riverInfo;

    /**
     * Метод для инициализации полей, а также задание логики поведедения некоторых
     * элементов.
     * @param riverInfo информация о реке, введенная пользователем.
     */
    public void init(River riverInfo){
        this.riverInfo = riverInfo;
        if(riverInfo.riverDepth >= 0){ this.textFieldRiverDepth.setText(String.valueOf(riverInfo.riverDepth)); }
        textFieldRiverDepth.textProperty().addListener(new ValidationChangeListener(textFieldRiverDepth));

        /*Подготовка контейнеров с текстбоксами и лейблами внутри
        * для дальнейшего использования в ChoiceBox логике*/
        this.choiceBoxRiver.getItems().setAll("Малая река (Метод Элдера)", "Большая река (Метод Банзала)",
                "Равнина река (Метод Потапова)",  "Естественные течения (Метод Карушева)",
                "Естественные течения (Комбинированный метод)");
        this.choiceBoxRiver.setValue("Большая река (Метод Банзала)");

        Label labelRiverWidth = new Label("Ширина (м)");
        labelRiverWidth.setId("textWithTooltip");
        Tooltip.install(labelRiverWidth, new Tooltip("Средняя ширина реки (м)"));
        TextField textFieldRiverWidth = new TextField();
        if(riverInfo.riverWidth >= 0) { textFieldRiverWidth.setText(String.valueOf(riverInfo.riverWidth));}
        this.componentsOfGUI.put("textFieldRiverWidth", textFieldRiverWidth);
        textFieldRiverWidth.textProperty().addListener(new ValidationChangeListener(textFieldRiverWidth));
        VBox riverWidthContainer = new VBox(labelRiverWidth, textFieldRiverWidth);

        Label labelFlowSpeed = new Label("Скорость (м/c)");
        labelFlowSpeed.setId("textWithTooltip");
        Tooltip.install(labelFlowSpeed, new Tooltip("Средняя скорость течения реки (м/c)"));
        TextField textFieldFlowSpeed = new TextField();
        if(riverInfo.flowSpeed >= 0) { textFieldFlowSpeed.setText(String.valueOf(riverInfo.flowSpeed)); }
        textFieldFlowSpeed.textProperty().addListener(new ValidationChangeListener(textFieldFlowSpeed));
        this.componentsOfGUI.put("textFieldFlowSpeed", textFieldFlowSpeed);
        VBox flowSpeedContainer = new VBox(labelFlowSpeed, textFieldFlowSpeed);

        Label labelDeviation = new Label("Уклон");
        labelDeviation.setId("textWithTooltip");
        Tooltip.install(labelDeviation, new Tooltip("Уклон свободной поверхности реки"));
        TextField textFieldDeviation = new TextField();
        textFieldDeviation.textProperty().addListener(new ValidationChangeListener(textFieldDeviation));
        VBox deviationContainer = new VBox(labelDeviation, textFieldDeviation);

        Label labelTortuosity = new Label("Извилистость");
        labelTortuosity.setId("textWithTooltip");
        Tooltip.install(labelTortuosity, new Tooltip("Коэффициент извилистости русла"));
        TextField textFieldTortuosity = new TextField();
        textFieldTortuosity.textProperty().addListener(new ValidationChangeListener(textFieldTortuosity));
        VBox tortuosityContainer = new VBox(labelTortuosity, textFieldTortuosity);

        Label labelRoughness = new Label("Шероховатость");
        labelRoughness.setId("textWithTooltip");
        Tooltip.install(labelRoughness, new Tooltip("Коэффициент шероховатости русла"));
        TextField textFieldRoughness = new TextField();
        textFieldRoughness.textProperty().addListener(new ValidationChangeListener(textFieldRoughness));
        VBox roughnessContainer = new VBox(labelRoughness, textFieldRoughness);
        /*_________________________________________________________________*/


        flowPane.getChildren().addAll(riverWidthContainer, flowSpeedContainer);

        //Изменение графического интерфейса, согласно выбранному режиму рассчета
        choiceBoxRiver.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            //Удаление элементов, пренадлежащих предыдущему методу рассчета
            switch (oldValue){
                case "Большая река (Метод Банзала)":
                    flowPane.getChildren().remove(riverWidthContainer);
                    flowPane.getChildren().remove(flowSpeedContainer);
                    componentsOfGUI.remove("textFieldRiverWidth");
                    componentsOfGUI.remove("textFieldFlowSpeed");
                    break;
                case "Малая река (Метод Элдера)":
                    flowPane.getChildren().removeAll(riverWidthContainer, deviationContainer);
                    this.componentsOfGUI.remove("textFieldRiverWidth");
                    this.componentsOfGUI.remove("textFieldDeviation");
                    break;
                case "Равнина река (Метод Потапова)":
                    flowPane.getChildren().removeAll(flowSpeedContainer);
                    this.componentsOfGUI.remove("textFieldFlowSpeed");
                    break;
                case "Естественные течения (Метод Карушева)":
                    flowPane.getChildren().removeAll(flowSpeedContainer, deviationContainer);
                    this.componentsOfGUI.remove("textFieldDeviation");
                    this.componentsOfGUI.remove("textFieldFlowSpeed");
                    break;
                case "Естественные течения (Комбинированный метод)":
                    flowPane.getChildren().removeAll(riverWidthContainer, flowSpeedContainer, tortuosityContainer, roughnessContainer);
                    this.componentsOfGUI.remove("textFieldRiverWidth");
                    this.componentsOfGUI.remove("textFieldFlowSpeed");
                    this.componentsOfGUI.remove("textFieldTortuosity");
                    this.componentsOfGUI.remove("textFieldRoughness");
                    break;
            }

            //Добавление компонентов графического интерфейса, которые понадобятся для рассчета
            switch (newValue){
                case "Большая река (Метод Банзала)":
                    flowPane.getChildren().addAll(riverWidthContainer, flowSpeedContainer);
                    this.componentsOfGUI.put("textFieldRiverWidth", textFieldRiverWidth);
                    this.componentsOfGUI.put("textFieldFlowSpeed", textFieldFlowSpeed);
                    break;
                case "Малая река (Метод Элдера)":
                    flowPane.getChildren().addAll(riverWidthContainer, deviationContainer);
                    this.componentsOfGUI.put("textFieldRiverWidth", textFieldRiverWidth);
                    this.componentsOfGUI.put("textFieldDeviation", textFieldDeviation);
                    break;
                case "Равнина река (Метод Потапова)":
                    flowPane.getChildren().addAll(flowSpeedContainer);
                    this.componentsOfGUI.put("textFieldFlowSpeed", textFieldFlowSpeed);
                    break;
                case "Естественные течения (Метод Карушева)":
                    flowPane.getChildren().addAll(flowSpeedContainer, deviationContainer);
                    this.componentsOfGUI.put("textFieldDeviation", textFieldDeviation);
                    this.componentsOfGUI.put("textFieldFlowSpeed", textFieldFlowSpeed);
                    break;
                case "Естественные течения (Комбинированный метод)":
                    flowPane.getChildren().addAll(riverWidthContainer, flowSpeedContainer, tortuosityContainer, roughnessContainer);
                    this.componentsOfGUI.put("textFieldRiverWidth", textFieldRiverWidth);
                    this.componentsOfGUI.put("textFieldFlowSpeed", textFieldFlowSpeed);
                    this.componentsOfGUI.put("textFieldTortuosity", textFieldTortuosity);
                    this.componentsOfGUI.put("textFieldRoughness", textFieldRoughness);
                    break;
            }
            for (Map.Entry<String, Node> entry: componentsOfGUI.entrySet()) {
                System.out.println(entry.getKey() + " ");
            }
            System.out.println();
        });
    }


    /**
     * Метод, решающий задачу, согласно введеннымы даннымы от пользователя и выводит результат в TextBox
     */
    public void calculate() {

        try {
            switch (this.choiceBoxRiver.getValue()) {
                case "Большая река (Метод Банзала)": {
                    double depth = Double.parseDouble(textFieldRiverDepth.getText().replace(',', '.'));
                    TextField tempTextField = (TextField) componentsOfGUI.get("textFieldFlowSpeed");
                    double flowSpeed = Double.parseDouble(tempTextField.getText().replace(',', '.'));
                    tempTextField = (TextField) componentsOfGUI.get("textFieldRiverWidth");
                    double width = Double.parseDouble(tempTextField.getText().replace(',', '.'));
                    textFieldResult.setText(String.valueOf(RiverMath.methodBanzal(depth, flowSpeed, width)));
                    break;
                }
                case "Малая река (Метод Элдера)": {
                    double depth = Double.parseDouble(textFieldRiverDepth.getText().replace(',', '.'));
                    TextField tempTextField = (TextField) componentsOfGUI.get("textFieldDeviation");
                    double deviation = Double.parseDouble(tempTextField.getText().replace(',', '.'));
                    tempTextField = (TextField) componentsOfGUI.get("textFieldRiverWidth");
                    double width = Double.parseDouble(tempTextField.getText().replace(',', '.'));
                    textFieldResult.setText(String.valueOf(RiverMath.methodElder(deviation, depth, width)));
                    break;
                }
                case "Равнина река (Метод Потапова)": {
                    double depth = Double.parseDouble(textFieldRiverDepth.getText().replace(',', '.'));
                    TextField tempTextField = (TextField) componentsOfGUI.get("textFieldFlowSpeed");
                    double flowSpeed = Double.parseDouble(tempTextField.getText().replace(',', '.'));
                    textFieldResult.setText(String.valueOf(RiverMath.methodPotapov(depth, flowSpeed)));
                    break;
                }
                case "Естественные течения (Комбинированный метод)": {
                    double depth = Double.parseDouble(textFieldRiverDepth.getText().replace(',', '.'));
                    TextField tempTextField = (TextField) componentsOfGUI.get("textFieldFlowSpeed");
                    double flowSpeed = Double.parseDouble(tempTextField.getText().replace(',', '.'));
                    tempTextField = (TextField) componentsOfGUI.get("textFieldRiverWidth");
                    double width = Double.parseDouble(tempTextField.getText().replace(',', '.'));
                    tempTextField = (TextField) componentsOfGUI.get("textFieldTortuosity");
                    double tortuosity = Double.parseDouble(tempTextField.getText().replace(',', '.'));
                    tempTextField = (TextField) componentsOfGUI.get("textFieldRoughness");
                    double roughness = Double.parseDouble(tempTextField.getText().replace(',', '.'));
                    textFieldResult.setText(String.valueOf(RiverMath.combinedMethod(flowSpeed,
                            width, tortuosity, roughness, depth)));
                    break;
                }
                case "Естественные течения (Метод Карушева)": {
                    double depth = Double.parseDouble(textFieldRiverDepth.getText().replace(',', '.'));
                    TextField tempTextField = (TextField) componentsOfGUI.get("textFieldFlowSpeed");
                    double flowSpeed = Double.parseDouble(tempTextField.getText().replace(',', '.'));
                    tempTextField = (TextField) componentsOfGUI.get("textFieldDeviation");
                    double deviation = Double.parseDouble(tempTextField.getText().replace(',', '.'));
                    textFieldResult.setText(String.valueOf(RiverMath.methodKarushev(depth, flowSpeed, deviation)));
                    break;
                }
            }
        } catch (NumberFormatException e) {
            ApplicationFactory.getInstance().getAlert("Все поля должны быть заполнены").showAndWait();
        }


    }
}
