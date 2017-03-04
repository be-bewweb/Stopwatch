package be.bewweb.StopWatch.view;

import be.bewweb.StopWatch.controller.BaseController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class CategoryView extends Application {


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            FXMLLoader fxmlLoader = new FXMLLoader(classLoader.getResource("views/Category.fxml"));
            Parent root = (Parent) fxmlLoader.load();
            final BaseController myController = (BaseController) fxmlLoader.getController();
            myController.setStage(primaryStage);
            primaryStage.addEventHandler(WindowEvent.WINDOW_SHOWING, new EventHandler<WindowEvent>() {
                public void handle(WindowEvent window) {
                    myController.initialized();
                }
            });

            Scene scene = new Scene(root);
            primaryStage.getIcons().add(new Image("icon.png"));
            primaryStage.setScene(scene);
            primaryStage.setTitle("StopWatch");
            primaryStage.sizeToScene();
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
