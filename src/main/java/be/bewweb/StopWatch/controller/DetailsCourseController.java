package be.bewweb.StopWatch.controller;

import be.bewweb.StopWatch.dao.beans.Course;
import be.bewweb.StopWatch.dao.beans.Team;
import be.bewweb.StopWatch.dao.persistence.Repository.Repository;
import be.bewweb.StopWatch.exception.DatabaseException;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Optional;

import static be.bewweb.StopWatch.utils.Time.convertLocalTimeToUTC;
import static be.bewweb.StopWatch.utils.Time.getTimestamp;

/**
 * @author Quentin Lombat
 */

public class DetailsCourseController {

    @FXML
    private Label lblNumberTeam;
    @FXML
    private Label lblNumberTeamArrived;
    @FXML
    private Label lblNumberTeamNotArrived;
    @FXML
    private TextField txtTime;
    @FXML
    private Button btnStartCourse;

    private Course course;
    private Integer numberTeamArrived;
    private Integer numberTeamValidated;

    private Repository<Course> courseRepository;

    DetailsCourseController(Course course) {
        this.course = course;
    }

    @FXML
    public void initialize() {
        courseRepository = new Repository<>(Course.class);

        btnStartCourse.setOnAction(this::onClickBtnStartCourse);

        if (course.isStarted()) {
            disableTxtTime();
        }

        refreshCourse(course);
    }

    public Course getCourse() {
        return this.course;
    }

    void refreshCourse(Course course) {
        this.course = course;
        refreshNumberTeam();
        setLabelValue();
        if(course.getStartTime() != null){
            DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
            txtTime.setText(course.getStartTime().toString(formatter));
            disableTxtTime();
        }
    }

    private void disableTxtTime() {
        txtTime.setEditable(false);
        btnStartCourse.setVisible(false);
    }

    private void onClickBtnStartCourse(Event event) {
        if (!txtTime.getText().matches("[0-3]?[0-9]\\/[0-1]?[0-9]\\/[0-9]{4} [0-9]{0,2}:[0-9]{0,2}:[0-9]{0,2}")) {
            txtTime.setStyle("-fx-border-color: red");
            return;
        }

        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");

        for (Team team : course.getTeams()) {
            if (team.getStartTime().getMillis() == 0) {
                team.setStartTime(formatter.parseDateTime(txtTime.getText()));
            }
        }

        course.setStarted(true);
        course.setStartTime(formatter.parseDateTime(txtTime.getText()));

        disableTxtTime();

        updateCourse(course);
    }

    private void updateCourse(Course course) {
        try {
            courseRepository.merge(course);
        } catch (DatabaseException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Problème avec la base de données");
            alert.setHeaderText("Une erreur est survenue lors de l'accès à la base de données");
            alert.setContentText("Impossible de mettre à jour la course pour le moment.");

            ButtonType refresh = new ButtonType("Réessayer", ButtonBar.ButtonData.OK_DONE);

            alert.getButtonTypes().setAll(refresh);
            Optional<ButtonType> result = alert.showAndWait();


            if (result.get() == refresh) {
                updateCourse(course);
            }
        }
    }


    private String formatInteger(int i) {
        if (Math.abs(i) < 10) {
            return "00" + i;
        }
        if (Math.abs(i) < 100) {
            return "0" + i;
        }
        return i + "";
    }

    private void refreshNumberTeam() {
        numberTeamArrived = 0;
        numberTeamValidated = 0;
        for (Team team : course.getTeams()) {
            if (team.isRegistrationValidated()) {
                numberTeamValidated += 1;
                if (team.getEndTime().size() >= course.getNumberOfTurns()) {
                    numberTeamArrived += 1;
                }
            }
        }
    }

    private void setLabelValue() {
        lblNumberTeam.setText(formatInteger(numberTeamValidated));
        lblNumberTeamNotArrived.setText(formatInteger(numberTeamValidated - numberTeamArrived));
        lblNumberTeamArrived.setText(formatInteger(numberTeamArrived));
    }
}
