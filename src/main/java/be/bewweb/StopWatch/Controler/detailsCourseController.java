package be.bewweb.StopWatch.Controler;

import be.bewweb.StopWatch.Modele.Course;
import be.bewweb.StopWatch.Modele.Listener.CourseListener;
import be.bewweb.StopWatch.Modele.Listener.TeamListener;
import be.bewweb.StopWatch.Modele.Race;
import be.bewweb.StopWatch.Modele.Runner;
import be.bewweb.StopWatch.Modele.Team;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.text.ParseException;
import java.util.ArrayList;

import static be.bewweb.StopWatch.Utils.Time.convertLocalTimeToUTC;
import static be.bewweb.StopWatch.Utils.Time.getTimestamp;

/**
 * Created by Quentin on 08-02-16.
 */

public class detailsCourseController {

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

    public detailsCourseController(Course course) {
        this.course = course;
    }

    @FXML
    public void initialize() {
        btnStartCourse.setOnAction(event -> onClickBtnStartCourse(event));

        if (course.isStarted()) {
            disableTxtTime();
        }

        course.addListener(new CourseListener() {
            @Override
            public void nameChanged(String name) {

            }

            @Override
            public void kmChanged(float km) {

            }

            @Override
            public void teamAdded(Team team) {
                if (team.isRegistrationValidated()) {
                    numberTeamValidated += 1;
                    setLabelValue();
                }
                addTeamListener(team);
            }

            @Override
            public void teamRemoved(Team team) {
                initNumberTeam();
                setLabelValue();
            }
        });
        initNumberTeam();
        setLabelValue();
        initAllTeamListener();
    }

    private void disableTxtTime() {
        txtTime.setVisible(false);
        btnStartCourse.setVisible(false);
    }

    private void onClickBtnStartCourse(Event event) {
        if (!txtTime.getText().matches("[0-3]?[0-9]\\/[0-1]?[0-9]\\/[0-9]{4} [0-9]{0,2}:[0-9]{0,2}:[0-9]{0,2}")) {
            txtTime.setStyle("-fx-border-color: red");
            return;
        }

        try {
            long timestampUTC = getTimestamp(convertLocalTimeToUTC(txtTime.getText(), "dd/MM/yyyy HH:mm:ss", "dd/MM/yyyy HH:mm:ss.SSS"), "dd/MM/yyyy HH:mm:ss.SSS");

            for (Team team : course.getTeams()) {
                if (team.getStartTime() == 0) {
                    team.setStartTime(timestampUTC);
                }
            }

            course.setStarted(true);
            disableTxtTime();
            Race.getInstance().save();

        } catch (ParseException e) {
            e.printStackTrace();
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

    private void initNumberTeam() {
        numberTeamArrived = 0;
        numberTeamValidated = 0;
        for (Team team : course.getTeams()) {
            if (team.isRegistrationValidated()) {
                numberTeamValidated += 1;
                if (team.getEndTime().size() == course.getNumberOfTurns()) {
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

    private void addTeamListener(Team team) {
        team.addListener(new TeamListener() {
            @Override
            public void dossardChanged(int dossard) {

            }

            @Override
            public void startTimeChanged(long start) {

            }

            @Override
            public void endTimeChanged(ArrayList<Long> end) {
                initNumberTeam();
                setLabelValue();

            }

            @Override
            public void runner1Changed(Runner runner) {

            }

            @Override
            public void runner2Changed(Runner runner) {

            }

            @Override
            public void registrationValidatedChanged(Boolean registrationValidated) {
                initNumberTeam();
                setLabelValue();
            }
        });
    }

    private void initAllTeamListener() {
        for (Team team : course.getTeams()) {
            addTeamListener(team);
        }
    }
}
