package be.bewweb.StopWatch.controller;

import be.bewweb.StopWatch.dao.beans.Course;
import be.bewweb.StopWatch.dao.beans.Race;
import be.bewweb.StopWatch.dao.beans.Runner;
import be.bewweb.StopWatch.dao.beans.Team;
import be.bewweb.StopWatch.dao.persistence.HibernateUtil;
import be.bewweb.StopWatch.dao.persistence.Repository.Repository;
import be.bewweb.StopWatch.dao.persistence.Repository.TeamRepository;
import be.bewweb.StopWatch.exception.DatabaseException;
import be.bewweb.StopWatch.view.ManageTeamView;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.joda.time.DateTime;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static be.bewweb.StopWatch.utils.Number.round;

public class EncodeController extends BaseController {

    private final static int SECONDS_BETWEEN_TWO_SCAN = 10;

    @FXML
    private Button btnArrived;

    @FXML
    private TextField txtDossard;

    @FXML
    private Label lblInfo;

    @FXML
    private TableView tbListTeamLog;
    private ObservableList<TeamLog> tbListTeamLogData;


    private Race race;

    private TeamRepository teamRepository;
    private Repository<Race> raceRepository;

    private static Timer refreshRaceTimer;
    private ChangeListener refreshRaceListener;

    @FXML
    private CheckBox scrollToEnd;


    public void initialized() {
        teamRepository = new TeamRepository();
        raceRepository = new Repository<>(Race.class);

        this.race = (Race) getStage().getUserData();


        btnArrived.setOnAction(event -> onClickBtnArrived(event));
        txtDossard.setOnKeyPressed(event -> onKeyPressedTxtDossard(event));
        txtDossard.setOnAction(event -> onActionTxtDossard(event));
        btnArrived.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                onClickBtnArrived(event);
            }
        });

        tbListTeamLog.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                    if (mouseEvent.getClickCount() == 2) {
                        onClickShowTeam(mouseEvent);
                    }
                }
            }
        });

        initTimerRace();
        initDeleteItemTableView();
        initTableView();
        refreshTableViewValue();

    }

    private void initTimerRace() {
        refreshRaceTimer = new Timer(String.valueOf(Math.random()));
        refreshRaceTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                Platform.runLater(() -> {
                    refreshRace();
                });
            }
        }, 0, 5000);

        if (refreshRaceListener != null) {
            getStage().focusedProperty().removeListener(refreshRaceListener);
        }
        refreshRaceListener = new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
                System.out.printf(refreshRaceTimer.toString() + System.lineSeparator());
                refreshRaceTimer.cancel();
                refreshRaceTimer.purge();
                if (ov.getValue()) {
                    //focused
                    initTimerRace();
                }
            }
        };
        getStage().focusedProperty().addListener(refreshRaceListener);

    }


    private void refreshRace() {
        try {
            race = raceRepository.find(race.getId());
            refreshTableViewValue();
        } catch (DatabaseException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Problème avec la base de données");
            alert.setHeaderText("Une erreur est survenue lors de l'accès à la base de données");
            alert.setContentText("Impossible de récuperer la course pour le moment.");

            ButtonType refresh = new ButtonType("Réessayer", ButtonBar.ButtonData.OK_DONE);

            alert.getButtonTypes().setAll(refresh);
            Optional<ButtonType> result = alert.showAndWait();


            if (result.get() == refresh) {
                refreshRace();
            }

        }
    }


    private void initTableView() {
        tbListTeamLogData = FXCollections.observableArrayList();
        TableColumn dossardCol = new TableColumn("Dos");
        dossardCol.setCellValueFactory(new PropertyValueFactory<TeamLog, String>("bib"));
        dossardCol.setStyle("-fx-alignment: CENTER;");
        dossardCol.prefWidthProperty().bind(tbListTeamLog.widthProperty().divide(7));

        TableColumn courseCol = new TableColumn("Parcours");
        courseCol.setCellValueFactory(new PropertyValueFactory<TeamLog, String>("course"));
        courseCol.setStyle("-fx-alignment: CENTER;");
        courseCol.prefWidthProperty().bind(tbListTeamLog.widthProperty().divide(7));

        TableColumn runner1Col = new TableColumn("Runner 1");
        runner1Col.setCellValueFactory(new PropertyValueFactory<TeamLog, String>("runner1"));
        runner1Col.setStyle("-fx-alignment: CENTER;");
        runner1Col.prefWidthProperty().bind(tbListTeamLog.widthProperty().divide(7));

        TableColumn runner2Col = new TableColumn("Runner 2");
        runner2Col.setCellValueFactory(new PropertyValueFactory<TeamLog, String>("runner2"));
        runner2Col.setStyle("-fx-alignment: CENTER;");
        runner2Col.prefWidthProperty().bind(tbListTeamLog.widthProperty().divide(7));

        TableColumn turnCol = new TableColumn("Tours");
        turnCol.setCellValueFactory(new PropertyValueFactory<TeamLog, String>("turnStr"));
        turnCol.setStyle("-fx-alignment: CENTER;");
        turnCol.prefWidthProperty().bind(tbListTeamLog.widthProperty().divide(7));

        TableColumn timeCol = new TableColumn("Temps");
        timeCol.setCellValueFactory(new PropertyValueFactory<TeamLog, String>("durationStr"));
        timeCol.setStyle("-fx-alignment: CENTER;");
        timeCol.prefWidthProperty().bind(tbListTeamLog.widthProperty().divide(7));

        TableColumn speedCol = new TableColumn("Vitesse");
        speedCol.setCellValueFactory(new PropertyValueFactory<TeamLog, String>("speedStr"));
        speedCol.setStyle("-fx-alignment: CENTER;");
        speedCol.prefWidthProperty().bind(tbListTeamLog.widthProperty().divide(7));

        tbListTeamLog.getColumns().addAll(dossardCol, courseCol, runner1Col, runner2Col, turnCol, timeCol, speedCol);

        tbListTeamLog.setItems(tbListTeamLogData);

        //Scroll down
        tbListTeamLog.getItems().addListener(new ListChangeListener() {
            @Override
            public void onChanged(Change c) {
                final int size = tbListTeamLog.getItems().size();
                if (size > 0 && scrollToEnd.isSelected()) {
                    tbListTeamLog.scrollTo(size - 1);
                }
            }
        });

        tbListTeamLog.setRowFactory(tv -> new TableRow<TeamLog>() {
            @Override
            public void updateItem(TeamLog item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null) {
                    setStyle("");
                } else if (item.getSpeed() == 0f) {
                    setStyle("-fx-background-color: #bdc3c7;");
                } else if (item.getSpeed() > 19f) {
                    setStyle("-fx-background-color: #e74c3c;");
                }else if (item.getSpeed() > 18f) {
                    setStyle("-fx-background-color: #e67e22;");
                } else if (item.getSpeed() > 17f) {
                    setStyle("-fx-background-color: #f39c12;");
                } else if (item.getTurn() > item.getTeam().getCourse().getNumberOfTurns()) {
                    setStyle("-fx-background-color: #f1c40f;"); //Yello
                } else {
                    setStyle("");
                }
            }
        });


    }

    private void initDeleteItemTableView() {
        tbListTeamLog.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DELETE) {
                onTeamLogDeleted();
            }
        });
    }

    private void onTeamLogDeleted() {
        TeamLog teamLog = (TeamLog) tbListTeamLog.getSelectionModel().getSelectedItem();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Voulez-vous supprimer le temps de cette équipe ?");
        alert.setContentText("Vous souhaitez supprimer le tour numéro " + teamLog.getTurn() + " de l'équipe portant le dossard " + teamLog.getTeam().getBib() + ". Cette action est irréversible !");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            boolean deleted = false;
            tbListTeamLogData.remove(teamLog);
            for (Course course : race.getCourses()) {
                for (Team team : course.getTeams()) {
                    if (team.getBib() == teamLog.getTeam().getBib()) {
                        try {
                            team.getEndTime().remove(teamLog.getTurn() - 1);
                            teamRepository.merge(team);
                            tbListTeamLog.refresh();
                            deleted = true;
                        } catch (Exception e) {
                            //do nothing
                        }
                    }
                }
            }
            if (!deleted) {
                alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Temps non supprimé !");
                alert.setHeaderText("Impossible de supprimer le temps demandé !");
                alert.setContentText("Une erreur s'est produite lors de la suppression du temps demandé. Veuillez réessayer !");
                alert.show();
            }
        } else {
            // ... user chose CANCEL or closed the dialog
        }
    }

    private void refreshTableViewValue() {
        int indexSelected = tbListTeamLog.getSelectionModel().getSelectedIndex();


        tbListTeamLogData.clear();
        for (Course course : race.getCourses()) {
            for (Team team : course.getTeams()) {
                int turn = 1;
                for (DateTime endTime : team.getEndTime()) {
                    tbListTeamLogData.add(new TeamLog(team, turn));
                    turn++;
                }
            }
        }
        tbListTeamLogData.sort((o1, o2) -> (int) (o1.getTeam().getEndTime().get(o1.getTurn() - 1).getMillis() - o2.getTeam().getEndTime().get(o2.getTurn() - 1).getMillis()));

        tbListTeamLog.getSelectionModel().select(indexSelected);

    }

    private void UIDossard(String message, String color) {
        lblInfo.setText(message);
        txtDossard.setText("");
        txtDossard.setStyle("-fx-background-color: " + color);
        txtDossard.requestFocus();
    }

    private void onClickBtnArrived(Event event) {

        refreshRace();

        if (txtDossard.getText().equals("")) {
            UIDossard("Numéro de dossard nécessaire", "red");
            return;
        }


        try {
            int dossard = Integer.parseInt(txtDossard.getText());
            Team teamFound = null;
            Course courseFound = null;

            DateTime dateTime = HibernateUtil.getServerDateTime();


            Iterator<Course> courses = race.getCourses().iterator();
            while (courses.hasNext() && teamFound == null) {
                Course course = courses.next();
                Iterator<Team> teams = course.getTeams().iterator();
                while (teams.hasNext() && teamFound == null) {
                    Team team = teams.next();
                    if (dossard == team.getBib()) {
                        teamFound = team;
                        courseFound = course;
                    }
                }
            }

            if (teamFound == null) {
                teamFound = new Team();
                teamFound.setBib(dossard);
                teamFound.setRegistrationValidated(true);
                courseFound = race.getCourses().get(0);
                courseFound.getTeams().add(teamFound);
                teamFound.setCourse(courseFound);
                UIDossard("Dossard introuvable", "green");
            } else {
                if (teamFound.getEndTime().size() > 0 && teamFound.getEndTime().get(teamFound.getEndTime().size() - 1).getMillis() + SECONDS_BETWEEN_TWO_SCAN * 1000 > dateTime.getMillis()) {
                    UIDossard("Ce dossard a déjà été encodé !", "red");
                    return;
                } else if (teamFound.getEndTime().size() >= courseFound.getNumberOfTurns()) {
                    UIDossard("Attention, course déjà terminé !", "orange");
                } else {
                    UIDossard("OK", "green");
                }
            }

            teamFound.getEndTime().add(dateTime);
            tbListTeamLogData.add(new TeamLog(teamFound, teamFound.getEndTime().size()));

            teamRepository.merge(teamFound);

        } catch (Exception e) {
            UIDossard("Une erreur est survenue !", "red");
            e.printStackTrace();
        }
    }

    private void onKeyPressedTxtDossard(Event event) {
        txtDossard.getStyleClass().removeAll();
    }

    private void onActionTxtDossard(Event event) {
        onClickBtnArrived(event);
    }

    private void onClickShowTeam(Event event) {
        try {
            Stage stage = new Stage();
            TeamLog teamLog = (TeamLog) tbListTeamLog.getSelectionModel().getSelectedItem();
            stage.setUserData(teamLog.getTeam());
            new ManageTeamView().start(stage);
        } catch (NullPointerException e) {
            e.printStackTrace();
            //Not item selected
        }
    }


    public class TeamLog {
        private Team team;
        private int turn;

        public TeamLog(Team team, int turn) {
            this.team = team;
            this.turn = turn;
        }

        public Team getTeam() {
            return team;
        }

        public void setTeam(Team team) {
            this.team = team;
        }

        public int getBib() {
            return team.getBib();
        }

        public String getRunner1() {
            if (team.getRunner1() == null) {
                return "n/a";
            }
            return team.getRunner1().toString();
        }

        public String getRunner2() {
            if (team.getRunner2() == null) {
                return "n/a";
            }
            return team.getRunner2().toString();
        }

        public String getCourse() {
            return team.getCourse().toString();
        }


        public int getTurn() {
            return turn;
        }

        public String getTurnStr() {
            return turn + "/" + team.getCourse().getNumberOfTurns();
        }

        public String getDurationStr() {
            if (team.getTime(turn) == null) {
                return "n/a";
            }
            DateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            return sdf.format(team.getTime(turn));
        }

        public float getSpeed() {
            if (team.getTime(turn) == null) {
                return 0;
            }
            float distancePerTurn = team.getCourse().getKm() / team.getCourse().getNumberOfTurns();
            float currentDistance = distancePerTurn * turn;
            return currentDistance / (team.getTime(turn) / 1000f / 60f / 60f);
        }

        public String getSpeedStr() {
            if (team.getTime(turn) == null) {
                return "n/a";
            }

            return round(getSpeed(), 2) + "km/h";
        }


        public void setTurn(int turn) {
            this.turn = turn;
        }
    }

}
