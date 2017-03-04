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

public class HomeView extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        FXMLLoader fxmlLoader = new FXMLLoader(classLoader.getResource("views/Home.fxml"));
        Parent root = (Parent) fxmlLoader.load();
        final BaseController myController = (BaseController) fxmlLoader.getController();
        myController.setStage(primaryStage);
        primaryStage.addEventHandler(WindowEvent.WINDOW_SHOWING, new EventHandler<WindowEvent>() {
            public void handle(WindowEvent window) {
                myController.initialized();
            }
        });
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent e) {
                Platform.exit();
                System.exit(0);
            }
        });
        Scene scene = new Scene(root);
        primaryStage.getIcons().add(new Image("icon.png"));
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.sizeToScene();
        primaryStage.show();

    }
}
