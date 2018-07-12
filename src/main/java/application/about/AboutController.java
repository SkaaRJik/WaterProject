package application.about;

import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * Created by SkaaRJ on 10.04.2018.
 */
public class AboutController {
    Scene scene;
    public void init(Scene scene){
        this.scene = scene;
    }

    public void close(ActionEvent event) {
        ((Stage)scene.getWindow()).close();
    }
}
