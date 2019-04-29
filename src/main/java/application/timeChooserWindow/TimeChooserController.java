package application.timeChooserWindow;

import application.ApplicationFactory;
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
    private TextField textFielddt;

    @FXML
    private TextField textFieldEndTime;

    @FXML
    private CheckBox dontMakeCutsCheckBox;

    @FXML
    private CheckBox makeCutsEverySecondsCheckBox;

    @FXML
    private CheckBox makeCutsCheckBox;

    @FXML
    private TextField makeCutsEverySecondsTextField;

    @FXML
    private TextField makeCutsTextField;

    private double splitDt;

    double dt;
    double endTime;
    private boolean status;

    public void init(){
        textFielddt.textProperty().addListener(new ValidationChangeListener(textFielddt));
        textFieldEndTime.textProperty().addListener(new ValidationChangeListener(textFieldEndTime));

        makeCutsEverySecondsTextField.disableProperty().bind(makeCutsEverySecondsCheckBox.selectedProperty().not());
        makeCutsTextField.disableProperty().bind(makeCutsCheckBox.selectedProperty().not());

        makeCutsTextField.disableProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue){
                makeCutsTextField.setText("");
            }
        });

        makeCutsEverySecondsTextField.disableProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue){
                makeCutsEverySecondsTextField.setText("");
            }
        });

        dontMakeCutsCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue) {
                makeCutsEverySecondsCheckBox.setSelected(false);
                makeCutsCheckBox.setSelected(false);
            }
        });
        makeCutsEverySecondsCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue) {
                dontMakeCutsCheckBox.setSelected(false);
                makeCutsCheckBox.setSelected(false);
            }
        });
        makeCutsCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue) {
                makeCutsEverySecondsCheckBox.setSelected(false);
                dontMakeCutsCheckBox.setSelected(false);
            }
        });


    }

    public void updateData(double lenghtOfTheRiver, double averageFlowSpeed) {
        this.status = false;
        int predictionTime = 380;
        this.splitDt = predictionTime*60+1;
        textFieldEndTime.setText(String.valueOf(predictionTime));
    }

    public void confirm(){
        try {
            this.dt = Double.valueOf(this.textFielddt.getText());
            this.endTime = Double.valueOf(this.textFieldEndTime.getText());
            Stage stage = (Stage) textFielddt.getScene().getWindow();

            if(dontMakeCutsCheckBox.isSelected()){
                this.splitDt = endTime*60+1;
            } else if(makeCutsCheckBox.isSelected()){
                if(makeCutsTextField.getText().length() == 0 ) {
                    ApplicationFactory.getInstance().getAlert("Заполните поле " + makeCutsCheckBox.getText()).showAndWait();
                    return;
                }
                this.splitDt = (endTime*60) / Double.parseDouble(makeCutsTextField.getText());
                if(this.splitDt % this.dt != 0) {
                    this.splitDt = splitDt - (this.splitDt % this.dt);
                    ApplicationFactory.getInstance().getAlert("Временной промежуток нельзя разделить на " + makeCutsTextField.getText() + " частей\n" +
                            "Выбран промежуток " + Math.ceil((endTime*60) / this.splitDt) + " штук" ).showAndWait();
                }
            } else if (makeCutsEverySecondsCheckBox.isSelected()){
                double temp = Double.valueOf(makeCutsEverySecondsTextField.getText().replace(",", "."));
                if(temp % this.dt != 0){
                    ApplicationFactory.getInstance().getAlert("Выберите время для" + makeCutsEverySecondsCheckBox.getText() + " кратное вашему\n" +
                            this.textFielddt.getPromptText() ).showAndWait();
                    return;
                }
                this.splitDt = temp;
            }
            this.status = true;
            stage.close();
        } catch (NumberFormatException ex){
            ex.printStackTrace();
            ApplicationFactory.getInstance().getAlert("Заполняйте поля правильно! Формат чисел 0.0").showAndWait();
        }

    }

    public double getDt() {
        return dt;
    }

    public double getEndTime() {
        return endTime;
    }

    public boolean getStatus(){
        return this.status;
    }

    public double getSplitDt() {
        return this.splitDt;
    }
}
