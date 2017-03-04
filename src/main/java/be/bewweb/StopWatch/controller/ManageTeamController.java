package be.bewweb.StopWatch.controller;

import be.bewweb.StopWatch.dao.beans.Course;
import be.bewweb.StopWatch.dao.beans.Race;
import be.bewweb.StopWatch.dao.beans.Runner;
import be.bewweb.StopWatch.dao.beans.Team;
import be.bewweb.StopWatch.exception.DatabaseException;
import be.bewweb.StopWatch.view.ManageTeamView;
import be.bewweb.StopWatch.dao.persistence.Repository.TeamRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

import static be.bewweb.StopWatch.utils.CheckUI.isNotEmpty;
import static be.bewweb.StopWatch.utils.Time.*;

/**
 * Created by Quentin on 06-02-16.
 */
public class ManageTeamController extends BaseController {

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

    @FXML
    private TableView tbEndTimes;
    private ObservableList<DateTime> tbEndTimesData;

    private Team currentTeam = null;

    private Race race;

    private TeamRepository teamRepository;

    DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
    DateTimeFormatter formatterWithMillis = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss.SSS");


    @Override
    public void initialized() {

        teamRepository = new TeamRepository();

        Object userData = getStage().getUserData();
        if (userData instanceof Team) {
            currentTeam = (Team) getStage().getUserData();
            getCurrentTeam();
            race = currentTeam.getCourse().getRace();
        } else if (userData instanceof Race) {
            race = (Race) getStage().getUserData();
            currentTeam = new Team();
        }

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

        cbCourse.setOnHidden(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                Course course = cbCourse.getSelectionModel().getSelectedItem();
                if (currentTeam.getCourse() != null && !Objects.equals(currentTeam.getCourse().getId(), course.getId()) && course.isStarted()) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Course déjà commencé !");
                    alert.setContentText("Vous avez choisi un parcours qui a déjà commencé ! Voulez-vous aussi changer l'heure de départ de cette équipe ? ");
                    ButtonType ok = new ButtonType("Oui", ButtonBar.ButtonData.OK_DONE);
                    ButtonType no = new ButtonType("Garder l'heure de départ actuelle", ButtonBar.ButtonData.NO);

                    alert.getButtonTypes().setAll(ok, no);
                    Optional<ButtonType> result = alert.showAndWait();


                    if (result.get() == ok) {
                        txtStartTime.setText(course.getStartTime().toString(formatter));
                    }

                } else if (currentTeam.getCourse() == null && course.isStarted()) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Course déjà commencé !");
                    alert.setContentText("Vous avez choisi un parcours qui a déjà commencé ! Voulez-vous utiliser l'heure de départ de la course pour cette équipe ? ");
                    ButtonType ok = new ButtonType("Oui", ButtonBar.ButtonData.OK_DONE);
                    ButtonType no = new ButtonType("Non", ButtonBar.ButtonData.NO);

                    alert.getButtonTypes().setAll(ok, no);
                    Optional<ButtonType> result = alert.showAndWait();


                    if (result.get() == ok) {
                        txtStartTime.setText(course.getStartTime().toString(formatter));
                    }
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

        cbCourse.getItems().addAll(race.getCourses());
        cbSex1.getItems().add("Homme");
        cbSex1.getItems().add("Femme");
        cbSex2.getItems().add("Homme");
        cbSex2.getItems().add("Femme");

        initTableViewEndTimes();
        populateWithCurrentTeam();
    }

    private void initTableViewEndTimes() {
        tbEndTimes.setEditable(true);
        Callback<TableColumn, TableCell> cellFactory =
                new Callback<TableColumn, TableCell>() {

                    @Override
                    public TableCell call(TableColumn p) {
                        return new EditingCellEndTime();
                    }
                };

        TableColumn endTimeCol = new TableColumn("Heure d'arrivée");
        endTimeCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures, ObservableValue>() {
            @Override
            public ObservableValue call(TableColumn.CellDataFeatures cellData) {
                return new SimpleStringProperty(((DateTime) cellData.getValue()).toString(formatterWithMillis));
            }
        });
        endTimeCol.setCellFactory(cellFactory);
        endTimeCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent>() {
            @Override
            public void handle(TableColumn.CellEditEvent event) {
                DateTime dateTime = (DateTime) event.getRowValue();
                DateTime newDateTime = formatterWithMillis.parseDateTime((String) event.getNewValue());
                tbEndTimesData.remove(dateTime);
                tbEndTimesData.add(newDateTime);
                currentTeam.getEndTime().remove(dateTime);
                currentTeam.getEndTime().add(newDateTime);

                tbEndTimes.refresh();
            }
        });
        endTimeCol.prefWidthProperty().bind(tbEndTimes.widthProperty().divide(1));
        endTimeCol.setSortType(TableColumn.SortType.ASCENDING);

        tbEndTimes.getColumns().addAll(endTimeCol);
        tbEndTimes.setEditable(true);
        tbEndTimesData = FXCollections.observableArrayList();
        tbEndTimes.setItems(tbEndTimesData);


        tbEndTimes.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> source, Number oldWidth, Number newWidth) {
                //Don't show header
                Pane header = (Pane) tbEndTimes.lookup("TableHeaderRow");
                if (header.isVisible()) {
                    header.setMaxHeight(0);
                    header.setMinHeight(0);
                    header.setPrefHeight(0);
                    header.setVisible(false);
                }
            }
        });

    }

    private void getCurrentTeam() {
        try {
            currentTeam = teamRepository.find(currentTeam.getId());
        } catch (DatabaseException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Problème avec la base de données");
            alert.setHeaderText("Une erreur est survenue lors de l'accès à la base de données");
            alert.setContentText("Impossible de récuperer l'équipe selectionné pour le moment.");

            ButtonType refresh = new ButtonType("Réessayer", ButtonBar.ButtonData.OK_DONE);

            alert.getButtonTypes().setAll(refresh);
            Optional<ButtonType> result = alert.showAndWait();


            if (result.get() == refresh) {
                getCurrentTeam();
            }
        }
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

            team.setBib(Integer.parseInt(txtDossard.getText()));
            team.setRunner1(runner1);
            team.setRunner2(runner2);
            team.setRegistrationValidated(chbRegistrationValidated.isSelected());


            Course oldCourse = currentTeam.getCourse();
            Course course = cbCourse.getSelectionModel().getSelectedItem();


            if (oldCourse != null) {
                oldCourse.getTeams().remove(team);
            }
            course.getTeams().add(team);
            team.setCourse(course);

            if (course.isStarted() && txtStartTime.getText().equals("")) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Course déjà commencé !");
                alert.setContentText("Vous avez choisi un parcours qui a déjà commencé ! Voulez-vous utiliser l'heure de départ de la course pour cette équipe ? ");
                ButtonType ok = new ButtonType("Oui", ButtonBar.ButtonData.OK_DONE);
                ButtonType no = new ButtonType("Non", ButtonBar.ButtonData.NO);

                alert.getButtonTypes().setAll(ok, no);
                Optional<ButtonType> result = alert.showAndWait();


                if (result.get() == ok) {
                    txtStartTime.setText(course.getStartTime().toString(formatter));
                } else {
                    txtStartTime.setStyle("-fx-border-color: red");
                    return false;
                }
            }

            if (!txtStartTime.getText().equals("")) {
                team.setStartTime(formatter.parseDateTime(txtStartTime.getText()));
            }

            updateTeam(team);

            return true;
        }
        return false;
    }

    private void updateTeam(Team team) {
        try {
            teamRepository.merge(team);
        } catch (DatabaseException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Problème avec la base de données");
            alert.setHeaderText("Une erreur est survenue lors de l'accès à la base de données");
            alert.setContentText("Impossible de sauvegarder l'équipe pour le moment.");

            ButtonType refresh = new ButtonType("Réessayer", ButtonBar.ButtonData.OK_DONE);

            alert.getButtonTypes().setAll(refresh);
            Optional<ButtonType> result = alert.showAndWait();


            if (result.get() == refresh) {
                updateTeam(team);
            }
        }
    }


    private void populateWithCurrentTeam() {
        cbCourse.getSelectionModel().select(currentTeam.getCourse());
        if (currentTeam.getBib() != null) {
            txtDossard.setText(currentTeam.getBib().toString());
        }

        if (currentTeam.getRunner1() != null) {
            txtName1.setText(currentTeam.getRunner1().getName());
            txtFirstname1.setText(currentTeam.getRunner1().getFirstname());
            cbSex1.getSelectionModel().select(((currentTeam.getRunner1().isMan()) ? "Homme" : "Femme"));
            dateBorn1.setValue(LocalDateTime.ofInstant(currentTeam.getRunner1().getBirthDate().toInstant(), ZoneId.systemDefault()).toLocalDate());
        }

        if (currentTeam.getRunner2() != null) {
            txtName2.setText(currentTeam.getRunner2().getName());
            txtFirstname2.setText(currentTeam.getRunner2().getFirstname());
            cbSex2.getSelectionModel().select(((currentTeam.getRunner2().isMan()) ? "Homme" : "Femme"));
            dateBorn2.setValue(LocalDateTime.ofInstant(currentTeam.getRunner2().getBirthDate().toInstant(), ZoneId.systemDefault()).toLocalDate());
        }
        chbRegistrationValidated.selectedProperty().setValue(currentTeam.isRegistrationValidated());
        try {
            if (currentTeam.getStartTime().getMillis() == 0) {
                txtStartTime.setText("");
            } else {
                txtStartTime.setText(currentTeam.getStartTime().toString(formatter));
            }
        } catch (Exception e) {
            txtStartTime.setText("");
        }
        tbEndTimesData.clear();
        tbEndTimesData.addAll(currentTeam.getEndTime());
        tbEndTimes.refresh();
    }

    public void onClickBtnSaveAndNew(Event event) {
        if (saveTeam()) {
            Stage stage = new Stage();
            stage.setUserData(race);
            new ManageTeamView().start(stage);
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
            int bib = Integer.parseInt(txtDossard.getText());
            Team teamFound = findOneByRaceAndBib(race, bib);
            if (teamFound != null) {
                currentTeam = teamFound;
            }
            currentTeam.setBib(bib);
            populateWithCurrentTeam();
        } catch (Exception ignored) {

        }
    }

    private Team findOneByRaceAndBib(Race race, Integer bib) {
        try {
            return teamRepository.findOneByRaceAndBib(race, bib);
        } catch (DatabaseException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Problème avec la base de données");
            alert.setHeaderText("Une erreur est survenue lors de l'accès à la base de données");
            alert.setContentText("Impossible de sauvegarder l'équipe pour le moment.");

            ButtonType refresh = new ButtonType("Réessayer", ButtonBar.ButtonData.OK_DONE);

            alert.getButtonTypes().setAll(refresh);
            Optional<ButtonType> result = alert.showAndWait();


            if (result.get() == refresh) {
                findOneByRaceAndBib(race, bib);
            }
        }
        return null;
    }


    class EditingCellEndTime extends TableCell<XYChart.Data, String> {

        private TextField textField;

        public EditingCellEndTime() {
        }

        @Override
        public void startEdit() {

            super.startEdit();

            if (textField == null) {
                createTextField();
            }

            setGraphic(textField);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            textField.selectAll();
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();

            setText(String.valueOf(getItem()));
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        }

        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                if (isEditing()) {
                    if (textField != null) {
                        textField.setText(getString());
                    }
                    setGraphic(textField);
                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                } else {
                    setText(getString());
                    setContentDisplay(ContentDisplay.TEXT_ONLY);
                }
            }
        }

        @Override
        public void commitEdit(String newValue) {
            if (!textField.getText().matches("([0-3]?[0-9]\\/[0-1]?[0-9]\\/[0-9]{4} [0-9]{0,2}:[0-9]{0,2}:[0-9]{0,2}.[0-9]{3})?")) {
                textField.setStyle("-fx-border-color: red");
            } else {
                super.commitEdit(newValue);
            }
        }

        private void createTextField() {
            textField = new TextField(getString());
            textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
            textField.setOnKeyPressed(new EventHandler<KeyEvent>() {

                @Override
                public void handle(KeyEvent t) {
                    if (t.getCode() == KeyCode.ENTER) {
                        commitEdit(textField.getText());
                    } else if (t.getCode() == KeyCode.ESCAPE) {
                        cancelEdit();
                    }
                }
            });
        }

        private String getString() {
            return getItem() == null ? "" : getItem().toString();
        }
    }
}
