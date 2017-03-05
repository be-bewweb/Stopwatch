package be.bewweb.StopWatch.controller;

import be.bewweb.StopWatch.dao.beans.Course;
import be.bewweb.StopWatch.dao.beans.Race;
import be.bewweb.StopWatch.dao.beans.Team;
import be.bewweb.StopWatch.exception.DatabaseException;
import be.bewweb.StopWatch.utils.GenerateRanking;
import be.bewweb.StopWatch.view.*;
import be.bewweb.StopWatch.dao.persistence.Repository.Repository;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class HomeController extends BaseController {
    @FXML
    private Button btnAdd;
    @FXML
    private Button btnRemove;
    @FXML
    private Button btnShow;
    @FXML
    private Button btnEncode;
    @FXML
    private Button btnShowRanking;

    @FXML
    private TableView<Team> tbListTeam;
    private ObservableList<Team> tbListTeamData;
    @FXML
    private MenuItem menuItemModifyRace;
    @FXML
    private MenuItem menuItemGenerateAllRanking;
    @FXML
    private Label lblNameRace;

    @FXML
    private TabPane tpCourse;

    private Race race;

    private static Timer refreshRaceTimer;
    private static ChangeListener<Boolean> refreshRaceListener;

    private List<DetailsCourseController> detailsCourseControllers = new ArrayList<>();

    private Repository<Race> raceRepository;

    @Override
    public void initialized() {
        super.initialized();

        raceRepository = new Repository<>(Race.class);

        this.race = (Race) getStage().getUserData();

        //Event
        btnAdd.setOnAction(this::onClickBtnAdd);
        btnRemove.setOnAction(this::onClickBtnRemove);
        btnShow.setOnAction(this::onClickBtnShow);
        btnEncode.setOnAction(this::onClickBtnEncode);
        btnShowRanking.setOnAction(this::onClickBtnShowRanking);
        menuItemModifyRace.setOnAction(this::onClickMenuItemModifyRace);
        menuItemGenerateAllRanking.setOnAction(this::onClickMenuItemGenerateAllRanking);
        tbListTeam.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                if (mouseEvent.getClickCount() == 2) {
                    onClickBtnShow(mouseEvent);
                }
            }
        });
        tbListTeam.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DELETE) {
                onClickBtnRemove(event);
            }
        });
        btnShow.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                onClickBtnShow(event);
            }
        });
        btnAdd.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                onClickBtnAdd(event);
            }
        });
        btnEncode.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                onClickBtnEncode(event);
            }
        });
        btnRemove.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                onClickBtnRemove(event);
            }
        });
        btnShowRanking.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                onClickBtnShowRanking(event);
            }
        });

        startAutoRefresh();
        initTableView();
        initTabPane();

    }

    private void startAutoRefresh() {
        refreshRaceTimer = new Timer();
        refreshRaceTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                try {
                    race = raceRepository.find(race.getId());

                    Platform.runLater(() -> {
                        lblNameRace.setText(race.getName());
                        refreshListTeam();
                        refreshDetailsCourses();
                    });

                } catch (DatabaseException e) {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Problème avec la base de données");
                        alert.setHeaderText("Une erreur est survenue lors de l'accès à la base de données");
                        alert.setContentText("Impossible de réfraichir la liste des équipes pour le moment.");

                        ButtonType refresh = new ButtonType("Réessayer", ButtonBar.ButtonData.OK_DONE);

                        alert.getButtonTypes().setAll(refresh);
                        Optional<ButtonType> result = alert.showAndWait();


                        if (result.get() == refresh) {
                            stopAutoRefresh();
                            startAutoRefresh();
                        }
                    });
                }
            }
        }, 0, 5000);
        if (refreshRaceListener != null) {
            getStage().focusedProperty().removeListener(refreshRaceListener);
            refreshRaceListener = null;
        }

        refreshRaceListener = (ov, t, t1) -> {
            System.out.printf(refreshRaceTimer.toString() + System.lineSeparator());
            pauseAutoRefresh();
            if (ov.getValue()) {
                //focused
                startAutoRefresh();
            }
        };

        getStage().focusedProperty().addListener(refreshRaceListener);

    }

    private synchronized void stopAutoRefresh() {
        getStage().focusedProperty().removeListener(refreshRaceListener);
        refreshRaceListener = null;
        refreshRaceTimer.cancel();
        refreshRaceTimer.purge();
    }

    private synchronized void pauseAutoRefresh() {
        refreshRaceTimer.cancel();
        refreshRaceTimer.purge();
    }

    private void refreshListTeam() {
        if (tbListTeamData == null) {
            tbListTeamData = FXCollections.observableArrayList();
            tbListTeam.setItems(tbListTeamData);
        }

        int indexSelected = tbListTeam.getSelectionModel().getSelectedIndex();


        tbListTeamData.clear();
        for (Course course : race.getCourses()) {
            tbListTeamData.addAll(course.getTeams());
        }
        SortedList<Team> sortedItems = new SortedList<>(tbListTeamData);
        tbListTeam.setItems(sortedItems);
        sortedItems.comparatorProperty().bind(tbListTeam.comparatorProperty());

        tbListTeam.getSelectionModel().select(indexSelected);
    }

    private void initTableView() {
        TableColumn<Team, String> dossardCol = new TableColumn<>("Dos");
        dossardCol.setCellValueFactory(new PropertyValueFactory<>("bib"));
        dossardCol.prefWidthProperty().bind(tbListTeam.widthProperty().divide(8));
        dossardCol.setSortType(TableColumn.SortType.ASCENDING);

        TableColumn<Team, String> runner1Col = new TableColumn<>("Runner 1");
        runner1Col.setCellValueFactory(new PropertyValueFactory<>("runner1"));
        runner1Col.prefWidthProperty().bind(tbListTeam.widthProperty().divide(8));

        TableColumn<Team, String> runner1AgeCol = new TableColumn<>("Age 1");
        runner1AgeCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getRunner1().getAge() + " ans"));
        runner1AgeCol.prefWidthProperty().bind(tbListTeam.widthProperty().divide(8).divide(2));

        TableColumn<Team, String> runner2Col = new TableColumn<>("Runner 2");
        runner2Col.setCellValueFactory(new PropertyValueFactory<>("runner2"));
        runner2Col.prefWidthProperty().bind(tbListTeam.widthProperty().divide(8));

        TableColumn<Team, String> runner2AgeCol = new TableColumn<>("Age 2");
        runner2AgeCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getRunner2().getAge() + " ans"));
        runner2AgeCol.prefWidthProperty().bind(tbListTeam.widthProperty().divide(8).divide(2));

        TableColumn<Team, String> registrationValidatedCol = new TableColumn<>("Inscription validé");
        registrationValidatedCol.setCellValueFactory(new PropertyValueFactory<>("registrationValidated"));
        registrationValidatedCol.prefWidthProperty().bind(tbListTeam.widthProperty().divide(8));

        TableColumn<Team, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeCol.prefWidthProperty().bind(tbListTeam.widthProperty().divide(8));

        TableColumn<Team, String> arrivedCol = new TableColumn<>("Arrivé");
        arrivedCol.setCellValueFactory(new PropertyValueFactory<>("arrived"));
        arrivedCol.prefWidthProperty().bind(tbListTeam.widthProperty().divide(8));

        TableColumn<Team, String> courseCol = new TableColumn<>("Course");
        courseCol.setCellValueFactory(new PropertyValueFactory<>("course"));
        courseCol.prefWidthProperty().bind(tbListTeam.widthProperty().divide(8));

        tbListTeam.getColumns().addAll(dossardCol, runner1Col, runner1AgeCol, runner2Col, runner2AgeCol, registrationValidatedCol, typeCol, arrivedCol, courseCol);
    }

    private void refreshDetailsCourses() {
        for (DetailsCourseController detailsCourseController : detailsCourseControllers) {
            for (Course course : race.getCourses()) {
                if (Objects.equals(course.getId(), detailsCourseController.getCourse().getId())) {
                    detailsCourseController.refreshCourse(course);
                    break;
                }
            }
        }
    }

    private void initTabPane() {
        tpCourse.getTabs().removeAll();
        for (Course course : race.getCourses()) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/DetailsCourse.fxml"));
                fxmlLoader.setController(new DetailsCourseController(course));
                detailsCourseControllers.add(fxmlLoader.getController());
                Tab tab = new Tab();
                tab.setText(course.toString());
                tab.setContent(fxmlLoader.load());
                tpCourse.getTabs().add(tab);
            } catch (IOException e) {
                e.printStackTrace();
                //Do nothing
            }
        }
    }

    private void onClickBtnAdd(Event event) {
        Stage stage = new Stage();
        stage.setUserData(race);
        new ManageTeamView().start(stage);
    }

    private void onClickBtnRemove(Event event) {
        stopAutoRefresh();
        Team team = tbListTeam.getSelectionModel().getSelectedItem();
        if (team != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Vous êtes sur le points de supprimer une équipe !");
            alert.setContentText("Attention, cette équipe sera définitivement supprimée. Souhaitez-vous la supprimer ?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                removeTeam(team);
            } else {
                // ... user chose CANCEL or closed the dialog
            }
        }
        startAutoRefresh();
    }

    private void removeTeam(Team team) {
        try {
            team.setCourse(null);
            for (Course course : race.getCourses()) {
                course.getTeams().remove(team);
            }
            raceRepository.merge(race);
            tbListTeamData.remove(team);
        } catch (DatabaseException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Problème avec la base de données");
            alert.setHeaderText("Une erreur est survenue lors de l'accès à la base de données");
            alert.setContentText("Impossible de supprimer l'équipe selectionné pour le moment.");

            ButtonType refresh = new ButtonType("Réessayer", ButtonBar.ButtonData.OK_DONE);

            alert.getButtonTypes().setAll(refresh);
            Optional<ButtonType> result = alert.showAndWait();


            if (result.get() == refresh) {
                removeTeam(team);
            }
        }
    }

    private void onClickBtnShow(Event event) {
        try {
            Stage stage = new Stage();
            stage.setUserData(tbListTeam.getSelectionModel().getSelectedItem());
            new ManageTeamView().start(stage);
        } catch (NullPointerException e) {
            e.printStackTrace();
            //Not item selected
        }
    }

    private void onClickBtnEncode(Event event) {
        Stage stage = new Stage();
        stage.setUserData(race);
        new EncodeView().start(stage);
    }

    private void onClickBtnShowRanking(Event event) {
        Stage stage = new Stage();
        stage.setUserData(race);
        new RankingView().start(stage);
    }

    private void onClickMenuItemModifyRace(Event event) {
        Stage stage = new Stage();
        stage.setUserData(race);
        new ParametersRaceView().start(stage);
        getStage().close();
    }

    private void onClickMenuItemGenerateAllRanking(Event event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File dirSelected = directoryChooser.showDialog(new Stage());
        if (dirSelected != null) {
            generateAllRanking(dirSelected.getAbsolutePath());
        }
    }

    private void generateAllRanking(String pathDir) {
        for (Course course : race.getCourses()) {
            new GenerateRanking(course).generate(pathDir);
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Classement généré");
        alert.setHeaderText("Classement généré !");
        alert.setContentText("Tous les classements ont été généré. Souhaitez-vous ouvrir le dossier dans l'explorateur ?");
        alert.getButtonTypes().remove(ButtonType.OK);
        alert.getButtonTypes().remove(ButtonType.CANCEL);
        alert.getButtonTypes().add(ButtonType.YES);
        alert.getButtonTypes().add(ButtonType.NO);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.YES) {
            try {
                Desktop.getDesktop().open(new File(pathDir));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // ... user chose CANCEL or closed the dialog
        }
    }
}
