package application;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
import utils.Validator;

/**
 * Created by SkaaRJ on 06.04.2018.
 */
public class ValidationChangeListener implements ChangeListener {
    TextField textField;

    public ValidationChangeListener(TextField textField) {
        this.textField = textField;
    }

    @Override
    public void changed(ObservableValue observable, Object oldValue, Object newValue) {
        if(!Validator.isOnlyNumbers((String) newValue)){
            ApplicationFactory.getInstance().getAlert("Поле должно содержать только цифры!").showAndWait();
            this.textField.setText((String) oldValue);
            return;
        }
        this.textField.setText(((String) newValue).replace(",", "."));
    }
}