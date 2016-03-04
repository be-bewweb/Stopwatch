package be.bewweb.StopWatch.View;

import be.bewweb.StopWatch.Controler.baseController;
import javafx.application.Application;
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
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/be/bewweb/StopWatch/View/Category.fxml"));
            Parent root = (Parent) fxmlLoader.load();
            final baseController myController = (baseController) fxmlLoader.getController();
            myController.setStage(primaryStage);
            primaryStage.addEventHandler(WindowEvent.WINDOW_SHOWING, new EventHandler<WindowEvent>() {
                public void handle(WindowEvent window) {
                    myController.initialized();
                }
            });

            Scene scene = new Scene(root);
            primaryStage.getIcons().add(new Image("/be/bewweb/StopWatch/resources/icon.png"));
            primaryStage.setScene(scene);
            primaryStage.setTitle("StopWach");
            primaryStage.sizeToScene();
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
