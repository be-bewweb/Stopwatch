package be.bewweb.StopWatch.Controler;

import be.bewweb.StopWatch.Modele.Course;
import be.bewweb.StopWatch.Modele.Race;
import be.bewweb.StopWatch.Modele.Runner;
import be.bewweb.StopWatch.Modele.Team;
import be.bewweb.StopWatch.View.ManageTeamView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

import static be.bewweb.StopWatch.Utils.CheckUI.isNotEmpty;
import static be.bewweb.StopWatch.Utils.Time.*;

/**
 * Created by Quentin on 06-02-16.
 */
public class ManageTeamController extends baseController {

    @FXML
    private Button btnSaveAndNew;
    @FXML
    private Button btnSaveAndClose;
    @FXML
    private Button btnClose;
    @FXML
    private TextField txtDossard;
    @FXML
    private TextField txtName1;
    @FXML
    private TextField txtName2;

    @FXML
    private TextField txtFirstname1;
    @FXML
    private TextField txtFirstname2;
    @FXML
    private TextField txtStartTime;

    @FXML
    private ComboBox<Course> cbCourse;
    @FXML
    private ComboBox<String> cbSex1;
    @FXML
    private ComboBox<String> cbSex2;
    @FXML
    private DatePicker dateBorn1;
    @FXML
    private DatePicker dateBorn2;
    @FXML
    private CheckBox chbRegistrationValidated;

    private Team currentTeam = null;

    @FXML
    public void initialize() {
        btnSaveAndNew.setOnAction(event -> onClickBtnSaveAndNew(event));
        btnSaveAndClose.setOnAction(event -> onClickBtnSaveAndClose(event));
        btnClose.setOnAction(event -> onClickBtnClose(event));
        txtDossard.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {

                } else {
                    onFocusLostTxtDossard();
                }
            }
        });
        btnClose.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                onClickBtnClose(event);
            }
        });
        btnSaveAndClose.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                onClickBtnSaveAndClose(event);
            }
        });
        btnSaveAndNew.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                onClickBtnSaveAndNew(event);
            }
        });
        chbRegistrationValidated.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
               chbRegistrationValidated.setSelected(!chbRegistrationValidated.isSelected());
            }
        });

        cbCourse.getItems().addAll(Race.getInstance().getCourses());
        cbSex1.getItems().add("Homme");
        cbSex1.getItems().add("Femme");
        cbSex2.getItems().add("Homme");
        cbSex2.getItems().add("Femme");
    }

    @Override
    public void initialized() {
        loadTeam((Team) getStage().getUserData());
    }

    public void onClickBtnClose(Event event) {
        getStage().close();
    }

    private boolean saveTeam() {
        if (!txtStartTime.getText().matches("([0-3]?[0-9]\\/[0-1]?[0-9]\\/[0-9]{4} [0-9]{0,2}:[0-9]{0,2}:[0-9]{0,2})?")) {
            txtStartTime.setStyle("-fx-border-color: red");
            return false;
        }

        if (isNotEmpty(txtName1) && isNotEmpty(txtName2) && isNotEmpty(txtFirstname1) && isNotEmpty(txtFirstname2) && isNotEmpty(txtDossard) && isNotEmpty(dateBorn1) && isNotEmpty(dateBorn2) && isNotEmpty(cbCourse) && isNotEmpty(cbSex1) && isNotEmpty(cbSex2)) {
            try {
                Runner runner1 = (currentTeam != null && currentTeam.getRunner1() != null) ? currentTeam.getRunner1() : new Runner();
                Runner runner2 = (currentTeam != null && currentTeam.getRunner2() != null) ? currentTeam.getRunner2() : new Runner();
                Team team = (currentTeam != null) ? currentTeam : new Team();

                runner1.setName(txtName1.getText());
                runner1.setFirstname(txtFirstname1.getText());
                runner1.setMan(cbSex1.getSelectionModel().getSelectedItem().equals("Homme"));
                runner1.setBirthDate(Date.from(dateBorn1.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));

                runner2.setName(txtName2.getText());
                runner2.setFirstname(txtFirstname2.getText());
                runner2.setMan(cbSex2.getSelectionModel().getSelectedItem().equals("Homme"));
                runner2.setBirthDate(Date.from(dateBorn2.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));

                team.setDossard(Integer.parseInt(txtDossard.getText()));
                team.setRunner1(runner1);
                team.setRunner2(runner2);
                team.setRegistrationValidated(chbRegistrationValidated.isSelected());

                if (!txtStartTime.getText().equals("")) {
                    long timestampUTC = getTimestamp(convertLocalTimeToUTC(txtStartTime.getText(), "dd/MM/yyyy HH:mm:ss", "dd/MM/yyyy HH:mm:ss.SSS"), "dd/MM/yyyy HH:mm:ss.SSS");
                    team.setStartTime(timestampUTC);
                }

                Team teamToSupress = null;
                Course courseToSupress = null;
                ArrayList<Course> lstCourse = Race.getInstance().getCourses();
                for (Course course : lstCourse) {
                    ArrayList<Team> c =course.getTeams();
                    for (Team t : c) {
                        if (t.getDossard() == currentTeam.getDossard()) {
                            teamToSupress = t;
                            courseToSupress = course;
                        }
                    }
                }
                Course course = cbCourse.getSelectionModel().getSelectedItem();

                if(courseToSupress != null){
                    courseToSupress.removeTeam(teamToSupress);
                    course.addTeam(teamToSupress);
                }



                if (currentTeam == null) {
                    course.addTeam(team);
                }
                Race.getInstance().save();
                return true;

            } catch (ParseException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    private void loadTeam(Team team) {
        for (Course course : Race.getInstance().getCourses()) {
            for (Team t : course.getTeams()) {
                try {
                    if (t == team) {
                        currentTeam = team;
                        cbCourse.getSelectionModel().select(course);
                        txtDossard.setText(team.getDossard() + "");

                        if(team.getRunner1() != null){
                            txtName1.setText(team.getRunner1().getName());
                            txtFirstname1.setText(team.getRunner1().getFirstname());
                            cbSex1.getSelectionModel().select(((team.getRunner1().isMan()) ? "Homme" : "Femme"));
                            dateBorn1.setValue(LocalDateTime.ofInstant(team.getRunner1().getBirthDate().toInstant(), ZoneId.systemDefault()).toLocalDate());
                        }

                        if(team.getRunner2() != null){
                            txtName2.setText(team.getRunner2().getName());
                            txtFirstname2.setText(team.getRunner2().getFirstname());
                            cbSex2.getSelectionModel().select(((team.getRunner2().isMan()) ? "Homme" : "Femme"));
                            dateBorn2.setValue(LocalDateTime.ofInstant(team.getRunner2().getBirthDate().toInstant(), ZoneId.systemDefault()).toLocalDate());
                        }
                        chbRegistrationValidated.selectedProperty().setValue(team.isRegistrationValidated());
                        try {
                            txtStartTime.setText(convertUTCToLocalTime(getUTCTime(team.getStartTime(), "dd/MM/yyyy HH:mm:ss.SSS"), "dd/MM/yyyy HH:mm:ss.SSS", "dd/MM/yyyy HH:mm:ss"));
                        } catch (ParseException e) {
                            txtStartTime.setText("");
                        }

                        return;
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void onClickBtnSaveAndNew(Event event) {
        if (saveTeam()) {
            new ManageTeamView().start(new Stage());
            onClickBtnClose(event);
        }
    }

    public void onClickBtnSaveAndClose(Event event) {
        if (saveTeam()) {
            onClickBtnClose(event);
        }
    }

    public void onFocusLostTxtDossard() {
        try {
            int dossard = Integer.parseInt(txtDossard.getText());
            for (Course course : Race.getInstance().getCourses()) {
                for (Team team : course.getTeams()) {
                    if (team.getDossard() == dossard) {
                        loadTeam(team);
                    }
                }
            }
        } catch (NumberFormatException e) {
            //do nothing
        }

    }
}
