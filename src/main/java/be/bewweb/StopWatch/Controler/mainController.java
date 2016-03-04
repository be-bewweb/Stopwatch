package be.bewweb.StopWatch.Controler;

import be.bewweb.StopWatch.Modele.Race;
import be.bewweb.StopWatch.View.home.homeView;
import be.bewweb.StopWatch.View.parametersRaceView;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class mainController extends baseController {
    @FXML
    private ImageView btnImgCreateRace;

    @FXML
    private ImageView btnImgLoadRace;


    @FXML
    public void initialize(){
        btnImgCreateRace.setOnMouseClicked(event -> onClickBtnImgCreateRace(event));
        btnImgLoadRace.setOnMouseClicked(event -> onClickBtnImgLoadFile(event));
    }

    private void onClickBtnImgCreateRace(Event event){
        try {
            new parametersRaceView().start(new Stage());
            ((Stage) btnImgCreateRace.getScene().getWindow()).close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onClickBtnImgLoadFile(Event event){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Race Files", "*.race"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if(selectedFile != null){
            try {
                Race.getInstance().load(selectedFile.getAbsolutePath());
                new homeView().start(new Stage());
                ((Stage) btnImgCreateRace.getScene().getWindow()).close();
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
    }




}
