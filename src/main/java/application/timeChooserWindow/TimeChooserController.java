package application.timeChooserWindow;

import application.ValidationChangeListener;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Model;
import model.substance.Substance;
import utils.Validator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by SkaaRJ on 03.04.2018.
 */
public class TimeChooserController {

    @FXML
    TextField textFieldEndTime;
    @FXML
    TextField textFielddt;

    double dt;
    double endTime;

    public void init(double lenghtOfTheRiver, double averageFlowSpeed){
        this.endTime = 380;
        textFieldEndTime.setText(String.valueOf(this.endTime));
        textFielddt.textProperty().addListener(new ValidationChangeListener(textFielddt));
        textFieldEndTime.textProperty().addListener(new ValidationChangeListener(textFieldEndTime));
    }

    public void updateData(double lenghtOfTheRiver, double averageFlowSpeed) {
        int predictionTime = 380;
        textFieldEndTime.setText(String.valueOf(predictionTime));
    }

    public void confirm(){
        this.dt = Double.valueOf(this.textFielddt.getText());
        this.endTime = Double.valueOf(this.textFieldEndTime.getText());
        Stage stage = (Stage) textFielddt.getScene().getWindow();
        stage.close();
    }

    public double getDt() {
        return dt;
    }

    public double getEndTime() {
        return endTime;
    }
}
