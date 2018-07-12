package application.resultWindow;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Model;
import model.substance.Substance;

import java.io.File;
import java.io.IOException;

/**
 * Created by SkaaRJ on 03.04.2018.
 */
public class ResultWindow {
    Stage stage;
    ResultController controller;
    public ResultWindow(Scene parent, Model model) {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("view/ResultWindow.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.stage = new Stage();
        this.stage.setTitle("Результат");
        Scene scene = new Scene(root);
        scene.getStylesheets().add((getClass().getClassLoader().getResource("css/light.css")).toExternalForm());
        this. stage.setScene(scene);
        this.stage.initModality(Modality.APPLICATION_MODAL);

        this.stage.initModality(Modality.NONE);
        this.stage.initOwner(parent.getWindow());
        controller = loader.getController();
        this.controller.init(model);
    }

    public void show(){
        this.stage.showAndWait();
    }

    public void updateData(Model model){
        this.controller.updateModel(model);
    }


}
