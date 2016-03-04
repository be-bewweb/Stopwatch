package be.bewweb.StopWatch.Controler;

import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by Quentin on 08-02-16.
 */
public class baseController {
    private Stage stage;
    public void setStage(Stage stage) { this.stage = stage; }
    public Stage getStage(){ return this.stage;}
    public void initialized(){};
}
