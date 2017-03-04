package be.bewweb.StopWatch.controller;

import be.bewweb.StopWatch.exception.DatabaseException;
import be.bewweb.StopWatch.view.HomeView;
import be.bewweb.StopWatch.view.ParametersRaceView;
import be.bewweb.StopWatch.dao.beans.Race;
import be.bewweb.StopWatch.dao.persistence.Repository.Repository;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.Optional;

public class MainController extends BaseController {
    @FXML
    private Button btnNext;

    @FXML
    private ComboBox cbRaces;

    private Repository<Race> raceRepository;

    @FXML
    public void initialize() {
        raceRepository = new Repository<>(Race.class);

        cbRaces.getItems().add(0, "Nouvelle course ...");
        getAllRace();

        btnNext.setOnMouseClicked(event -> onClickBtnNext(event));
    }

    private void getAllRace(){
        try {
            cbRaces.getItems().addAll(raceRepository.findAll());
        } catch (DatabaseException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Problème avec la base de données");
            alert.setHeaderText("Une erreur est survenue lors de l'accès à la base de données");
            alert.setContentText("Impossible de récuperer toutes les courses pour le moment.");

            ButtonType refresh = new ButtonType("Réessayer", ButtonBar.ButtonData.OK_DONE);

            alert.getButtonTypes().setAll(refresh);
            Optional<ButtonType> result = alert.showAndWait();


            if (result.get() == refresh) {
                getAllRace();
            }
        }
    }

    private void onClickBtnNext(Event event) {
        Object objectSelected = cbRaces.getSelectionModel().getSelectedItem();
        try {
            if (objectSelected instanceof Race) {
                //Existing race
                Stage stage = new Stage();
                stage.setUserData(objectSelected);
                new HomeView().start(stage);
            } else {
                //New race
                new ParametersRaceView().start(new Stage());
            }
            ((Stage) this.getStage().getScene().getWindow()).close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
