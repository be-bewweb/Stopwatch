package be.bewweb.StopWatch.controller;

import javafx.stage.Stage;

/**
 * @author Quentin Lombat
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
