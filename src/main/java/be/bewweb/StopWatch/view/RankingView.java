package be.bewweb.StopWatch.view;/**
 * Created by Quentin on 06-02-16.
 */

import be.bewweb.StopWatch.controller.BaseController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class RankingView extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            FXMLLoader fxmlLoader = new FXMLLoader(classLoader.getResource("views/Ranking.fxml"));
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
            primaryStage.setMaximized(true);
            primaryStage.setFullScreen(true);
            scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    if(event.getCode() == KeyCode.F11){
                        primaryStage.setFullScreen(true);
                    }
                }
            });
            if (Screen.getScreens().size() > 1) {
                Screen screen = Screen.getScreens().get(1);
                Rectangle2D bounds = screen.getVisualBounds();

                primaryStage.setX(bounds.getMinX() + 100);
                primaryStage.setY(bounds.getMinY() + 100);
            }

            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
