package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Model;


/**
 * Класс, содержащий точку входа в программу.
 * <p>
 * Используется:
 * <p>
 * Технология графического интерфейса JavaFX.
 * <p>
 * Шаблоне проектирования MVC:
 * <p>
 *      Model - {@link Model}
 *      View - {resources/MainWindow.fxml}
 *      Controller - {@link Controller}
 * </p>
 */

public class Main extends Application {
    /**
     * Инициализация окна приложения.
     * Загружает MainWindow.fxml (представление графического интерфейса).
     * Выводит окно приложения на экран
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("view/MainWindow.fxml"));

        Parent root = loader.load();

        primaryStage.setTitle("Water");
        Scene scene = new Scene(root);
        scene.getStylesheets().add((getClass().getClassLoader().getResource("css/light.css")).toExternalForm());
        primaryStage.setScene(scene);

        Controller controller = loader.getController();
        controller.setScene(scene);
        primaryStage.show();
    }

    /**
     * Точка входа в программу, неявно вызывает {@link #start(Stage)}
     * @param args - параметры запуска(не используются)
     */
    public static void main(String[] args) {
        launch(args);
    }


}
