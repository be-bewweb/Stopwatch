package be.bewweb.StopWatch.controller;

import be.bewweb.StopWatch.dao.beans.Course;
import be.bewweb.StopWatch.dao.beans.Race;
import be.bewweb.StopWatch.dao.beans.Runner;
import be.bewweb.StopWatch.dao.beans.Team;
import be.bewweb.StopWatch.dao.persistence.Repository.Repository;
import be.bewweb.StopWatch.exception.DatabaseException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.WindowEvent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static be.bewweb.StopWatch.utils.Number.round;

public class RankingController extends BaseController {
    @FXML
    private TableView tbRanking;
    private ObservableList<Ranking> tbRankingData;

    private Race race;

    private static Timer refreshRaceTimer;
    private EventHandler windowEventListener;
    private Repository<Race> raceRepository;


    public void initialized() {

        this.race = (Race) getStage().getUserData();

        raceRepository = new Repository<>(Race.class);

        initTableView();
        initTimerRace();
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

        if (windowEventListener != null) {
            getStage().removeEventHandler(WindowEvent.ANY, windowEventListener);
        }
        windowEventListener = new EventHandler<WindowEvent>() {
            public void handle(WindowEvent window) {
                refreshRaceTimer.cancel();
                refreshRaceTimer.purge();
                if(window.getEventType() == WindowEvent.WINDOW_SHOWN && !window.isConsumed()){
                    initTimerRace();
                    window.consume();
                }
            }
        };

        getStage().addEventHandler(WindowEvent.ANY, windowEventListener);
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
        tbRankingData = FXCollections.observableArrayList();
        TableColumn dossardCol = new TableColumn("Dos");
        dossardCol.setCellValueFactory(new PropertyValueFactory<Ranking, String>("dossard"));
        dossardCol.setStyle("-fx-alignment: CENTER;");
        dossardCol.setStyle("-fx-font-size: 16;");
        dossardCol.prefWidthProperty().bind(tbRanking.widthProperty().divide(7));

        TableColumn runner1Col = new TableColumn("Runner 1");
        runner1Col.setCellValueFactory(new PropertyValueFactory<Ranking, String>("runner1"));
        runner1Col.setStyle("-fx-alignment: CENTER;");
        runner1Col.setStyle("-fx-font-size: 16;");
        runner1Col.prefWidthProperty().bind(tbRanking.widthProperty().divide(7));

        TableColumn runner2Col = new TableColumn("Runner 2");
        runner2Col.setCellValueFactory(new PropertyValueFactory<Ranking, String>("runner2"));
        runner2Col.setStyle("-fx-alignment: CENTER;");
        runner2Col.setStyle("-fx-font-size: 16;");
        runner2Col.prefWidthProperty().bind(tbRanking.widthProperty().divide(7));

        TableColumn courseCol = new TableColumn("Parcours");
        courseCol.setCellValueFactory(new PropertyValueFactory<Ranking, String>("course"));
        courseCol.setStyle("-fx-alignment: CENTER;");
        courseCol.setStyle("-fx-font-size: 16;");
        courseCol.prefWidthProperty().bind(tbRanking.widthProperty().divide(7));

        TableColumn rankingCourseCol = new TableColumn("Classement Parcours");
        rankingCourseCol.setCellValueFactory(new PropertyValueFactory<Ranking, String>("rankingCourse"));
        rankingCourseCol.setStyle("-fx-alignment: CENTER;");
        rankingCourseCol.setStyle("-fx-font-size: 16;");
        rankingCourseCol.prefWidthProperty().bind(tbRanking.widthProperty().divide(7));

        TableColumn averageSpeedCol = new TableColumn("Vitesse moyenne");
        averageSpeedCol.setCellValueFactory(new PropertyValueFactory<Ranking, String>("averageSpeed"));
        averageSpeedCol.setStyle("-fx-alignment: CENTER;");
        averageSpeedCol.setStyle("-fx-font-size: 16;");
        averageSpeedCol.prefWidthProperty().bind(tbRanking.widthProperty().divide(7));

        TableColumn timeCol = new TableColumn("Temps");
        timeCol.setCellValueFactory(new PropertyValueFactory<Ranking, String>("timeStr"));
        timeCol.setStyle("-fx-alignment: CENTER;");
        timeCol.setStyle("-fx-font-size: 16;");
        timeCol.prefWidthProperty().bind(tbRanking.widthProperty().divide(7));

        tbRanking.getColumns().addAll(dossardCol, runner1Col, runner2Col, courseCol, rankingCourseCol, averageSpeedCol, timeCol);

        tbRanking.setItems(tbRankingData);
    }

    private void refreshTableViewValue() {
        tbRankingData.clear();
        for (Course course : race.getCourses()) {
            ArrayList<Ranking> rankings = new ArrayList<>();
            for (Team team : course.getTeams()) {
                if (course.getNumberOfTurns() <= team.getEndTime().size() && team.getEndTime().size() > 0 && team.isRegistrationValidated()) {
                    Ranking ranking = new Ranking(team.getBib(), team.getRunner1(), team.getRunner2(), team.getStartTime().getMillis(), team.getEndTime().get(course.getNumberOfTurns() - 1).getMillis(), course);
                    tbRankingData.add(ranking);
                    rankings.add(ranking);
                }
            }
            rankings.sort((o1, o2) -> (int) (o1.getMillis() - o2.getMillis()));
            int rankingCourse = 1;
            for (Ranking item : rankings) {
                tbRankingData.get(tbRankingData.indexOf(item)).setRankingCourse(rankingCourse);
                rankingCourse++;

            }
        }
        tbRankingData.sort((o1, o2) -> (int) (o2.getEndTime() - o1.getEndTime()));


    }

    protected class Ranking {
        private int dossard;
        private Runner runner1;
        private Runner runner2;
        private long startTime;
        private long endTime;
        private Course course;
        private int rankingCourse;

        public Ranking(int dossard, Runner runner1, Runner runner2, long startTime, long endTime, Course course) {
            this.dossard = dossard;
            this.runner1 = runner1;
            this.runner2 = runner2;
            this.startTime = startTime;
            this.endTime = endTime;
            this.course = course;
        }

        public int getDossard() {
            return dossard;
        }

        public Runner getRunner1() {
            return runner1;
        }

        public Runner getRunner2() {
            return runner2;
        }

        public long getStartTime() {
            return startTime;
        }

        public long getEndTime() {
            return endTime;
        }

        public Course getCourse() {
            return course;
        }

        public String getAverageSpeed() {
            return round(((float) course.getKm()) / ((float) (endTime - startTime) / (float) (1000 * 60 * 60)), 2) + " km/h";
        }

        public int getRankingCourse() {
            return rankingCourse;
        }

        public void setRankingCourse(int rankingCourse) {
            this.rankingCourse = rankingCourse;
        }

        public String getTimeStr() {
            DateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            return sdf.format(endTime - startTime);
        }

        public long getMillis() {
            return endTime - startTime;
        }
    }
}
