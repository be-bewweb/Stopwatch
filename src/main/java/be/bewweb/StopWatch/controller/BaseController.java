package be.bewweb.StopWatch.controller;

import javafx.stage.Stage;

/**
 * Created by Quentin on 08-02-16.
 */
public class BaseController {
    private Stage stage;

    public Stage getStage() {
        return this.stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void initialized() {
    }
}
