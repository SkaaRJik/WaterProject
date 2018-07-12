package application.about;

import application.diffusionCalculatorWindow.DiffusionController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.river.River;

/**
 * Created by SkaaRJ on 10.04.2018.
 */
public class AboutWindow {
    Stage stage;
    AboutController controller;
    public AboutWindow(Scene parent) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("view/About.fxml"));
        Parent root = loader.load();
        this.stage = new Stage();
        this.stage.setTitle("О программе");
        Scene scene = new Scene(root);
        scene.getStylesheets().add((getClass().getClassLoader().getResource("css/light.css")).toExternalForm());
        this. stage.setScene(scene);
        this.stage.initModality(Modality.APPLICATION_MODAL);
        this.stage.initOwner(parent.getWindow());
        controller = loader.getController();
        controller.init(scene);
    }

    public void show(){
        this.stage.showAndWait();
    }
}
