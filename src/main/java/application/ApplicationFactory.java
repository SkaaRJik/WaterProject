package application;

import application.about.AboutWindow;
import application.diffusionCalculatorWindow.DiffusionApp;
import application.resultWindow.ResultWindow;
import application.timeChooserWindow.TimeChooserWindow;
import javafx.beans.property.StringProperty;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import model.Model;
import model.river.River;

/**
 * Фабрика окон.<br>
 * Позволит ссылки на окна, которыми пользовался пользователь.<br>
 * Если пользователь запускает окно впервые, или ссылка на окно была удалена сборщиком мусора,
 * то создается новый экземпляр класса.
 * <hr>
 * Пример использования: <br>
 * ApplicationFactory.getInstance().getDiffusionApp(parrent).show();
 */
public class ApplicationFactory {
    private static ApplicationFactory instance = null;
    private static DiffusionApp diffusionApp = null;
    private static ResultWindow resultWindow = null;
    private static TimeChooserWindow timeChooserWindow = null;
    private static AboutWindow aboutWindow = null;
    private static Alert alert = null;


    /**
     * Получаем ссылку на объект-фабрику
     * @return объект-фабрика
     */
    public static synchronized ApplicationFactory getInstance() {
        if(instance == null){
            instance = new ApplicationFactory();
        }
        return instance;
    }

    /**
     * Геттер окна калькулятора диффузии.
     * @param parent ссылка на родительскую сцену, которая вызыает окно.
     * @return
     */
    public DiffusionApp getDiffusionApp(Scene parent, StringProperty textFieldRiverWidthProperty, StringProperty textFieldRiverDepthProperty, StringProperty textFieldFlowSpeedProperty, StringProperty textFieldDiffusionCoefProperty){
        if(diffusionApp == null){
            try {
                diffusionApp = new DiffusionApp(parent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        diffusionApp.updateData(textFieldRiverWidthProperty, textFieldRiverDepthProperty, textFieldFlowSpeedProperty, textFieldDiffusionCoefProperty);
        return  diffusionApp;
    }

    /**
     * Геттер окна результата моделирования
     * @param parent ссылка на родительскую сцену, которая вызыает окно.
     * @param model модель, с готовым результатом
     * @return
     */
    public ResultWindow getResultWindow(Scene parent, Model model){
        if(resultWindow == null){
            try {
                resultWindow = new ResultWindow(parent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        resultWindow.updateData(model);
        return  resultWindow;
    }

    /**
     * Геттер окна выбора времени моделирования
     * @param parent ссылка на родительскую сцену, которая вызыает окно.
     * @param lenghtOfTheRiver длина реки
     * @param averageFlowSpeed средняя скорость реки
     * @return
     */
    public TimeChooserWindow getTimeChooserWindow(Scene parent, double lenghtOfTheRiver, double averageFlowSpeed){
        if(resultWindow == null){
            try {
                timeChooserWindow = new TimeChooserWindow(parent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        timeChooserWindow.updateData(lenghtOfTheRiver, averageFlowSpeed);

        return  timeChooserWindow;
    }

    /**
     * Геттер окна "О Программе"
     * @param parent ссылка на родительскую сцену, которая вызыает окно.
     * @return
     */
    public AboutWindow getAboutWindow(Scene parent){
        if(resultWindow == null){
            try {
                aboutWindow = new AboutWindow(parent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return  aboutWindow;
    }

    /**
     * Информационное окно об ошибке
     * @param message
     */
    public Alert getAlert(String message){
        if(alert == null) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText(null);
        }
        alert.setContentText(message);
        return alert;
    }

}
