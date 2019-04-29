package application.diffusionCalculatorWindow;

import application.Controller;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.river.River;

import java.awt.*;

/**
 * Created by SkaaRJ on 05.03.2018.
 */
public class DiffusionApp {
    Stage stage;
    DiffusionController controller;
    public DiffusionApp(Scene parent) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("view/DiffusionCalculator.fxml"));
        Parent root = loader.load();
        this.stage = new Stage();
        this.stage.setTitle("Калькулятор поперечной диффузии");
        Scene scene = new Scene(root);
        scene.getStylesheets().add((getClass().getClassLoader().getResource("css/light.css")).toExternalForm());
        this. stage.setScene(scene);

        this.stage.initModality(Modality.NONE);
        this.stage.initOwner(parent.getWindow());
        controller = loader.getController();
        controller.init();
    }

    public void updateData(StringProperty textFieldRiverWidthProperty, StringProperty textFieldRiverDepthProperty, StringProperty textFieldFlowSpeedProperty, StringProperty textFieldDiffusionCoefProperty){
        this.controller.updateBinds(textFieldRiverWidthProperty, textFieldRiverDepthProperty, textFieldFlowSpeedProperty, textFieldDiffusionCoefProperty);
    }

    public void show(){
        this.stage.show();
    }
}
