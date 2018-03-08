package application;

import application.diffusionCalculatorWindow.DiffusionApp;
import javafx.scene.Node;
import javafx.scene.Scene;
import model.river.River;

/**
 * Фабрика окон.<br>
 * Позволит ссылки на окна, которыми пользовался пользователь.<br>
 * Если пользователь запускает окно впервые, или ссылка на окно была удалена сборщиком мусора,
 * то создается новый объект класса.
 * <hr>
 * Пример использования: <br>
 * ApplicationFactory.getInstance().getDiffusionApp(parrent).show();
 */
public class ApplicationFactory {
    private static ApplicationFactory instance = null;
    private static DiffusionApp diffusionApp = null;

    public static synchronized ApplicationFactory getInstance() {
        if(instance == null){
            instance = new ApplicationFactory();
        }
        return instance;
    }


    public DiffusionApp getDiffusionApp(Scene parent, River riverInfo){
        if(diffusionApp == null){
            try {
                diffusionApp = new DiffusionApp(parent, riverInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return  diffusionApp;
    }

}
