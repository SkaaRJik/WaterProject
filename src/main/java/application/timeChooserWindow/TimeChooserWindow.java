package application.timeChooserWindow;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Model;
import model.substance.Substance;

import java.io.IOException;

/**
 * Created by SkaaRJ on 03.04.2018.
 */
public class TimeChooserWindow {
    Stage stage;
    TimeChooserController controller;




    public TimeChooserWindow(Scene parent) {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("view/TimeChooserWindow.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.stage = new Stage();
        this.stage.setTitle("Выбор времени");
        Scene scene = new Scene(root);
        scene.getStylesheets().add((getClass().getClassLoader().getResource("css/light.css")).toExternalForm());
        this. stage.setScene(scene);
        this.stage.initModality(Modality.APPLICATION_MODAL);
        this.stage.initModality(Modality.NONE);
        this.stage.initOwner(parent.getWindow());
        controller = loader.getController();
        this.controller.init( );
    }

    public void show(){
        this.stage.showAndWait();
    }

    public void updateData(double lenghtOfTheRiver, double averageFlowSpeed){
        this.controller.updateData(lenghtOfTheRiver,  averageFlowSpeed);
    }

    public double getDt() {
        return this.controller.getDt();
    }

    public double getEndTime() {
        return this.controller.getEndTime();
    }

    public boolean wasClosed(){
        return !this.controller.getStatus();
    }

    public double getSpliceDt() {
        return this.controller.getSplitDt();
    }
}
