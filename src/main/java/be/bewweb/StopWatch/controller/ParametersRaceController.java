package be.bewweb.StopWatch.controller;

import be.bewweb.StopWatch.dao.beans.Course;
import be.bewweb.StopWatch.dao.beans.Race;
import be.bewweb.StopWatch.exception.DatabaseException;
import be.bewweb.StopWatch.view.CategoryView;
import be.bewweb.StopWatch.view.HomeView;
import be.bewweb.StopWatch.dao.persistence.Repository.Repository;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
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

import java.util.Optional;

/**
 * Created by Quentin on 06-02-16.
 */
public class ParametersRaceController extends BaseController {

    @FXML
    private Button btnCreateRace;
    @FXML
    private Button btnCreateCourse;
    @FXML
    private Button btnRemoveCourse;
    @FXML
    private TextField txtNameOfRace;
    @FXML
    private TextField txtCourseName;
    @FXML
    private TextField txtCourseKm;
    @FXML
    private TextField txtCourseNumberOfTurns;
    @FXML
    private TableView tbListCourse;
    private ObservableList<Course> tbListCourseData;

    private Race currentRace;
    private Repository<Race> raceRepository;


    @Override
    public void initialized() {
        super.initialized();

        raceRepository = new Repository<>(Race.class);

        this.currentRace = (Race) getStage().getUserData();
        if (currentRace == null) {
            currentRace = new Race();
        } else {
            getCurrentRace();
        }

        btnCreateRace.setOnAction(event -> onClickBtnCreateRace(event));
        btnCreateCourse.setOnAction(event -> onClickBtnCreateCourse(event));
        btnRemoveCourse.setOnAction(event -> onClickBtnRemoveCourse(event));
        txtCourseName.setOnAction(event -> onActionTxtCourseName(event));
        txtCourseKm.setOnAction(event -> onActionTxtCourseKm(event));
        txtCourseNumberOfTurns.setOnAction(event -> onActionTxtCourseNumberOfTurns(event));
        tbListCourse.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                    if (mouseEvent.getClickCount() == 2) {
                        onclickOnItemTbListCourse(mouseEvent);
                    }
                }
            }
        });
        btnCreateCourse.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                onClickBtnCreateCourse(event);
            }
        });
        btnCreateRace.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                onClickBtnCreateRace(event);
            }
        });
        btnRemoveCourse.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                onClickBtnRemoveCourse(event);
            }
        });

        txtNameOfRace.setText(currentRace.getName());
        initTabelView();
        refreshTableViewData();

        getStage().focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
                if (ov.getValue()) {
                    //focused
                    refreshTableViewData();
                }
            }
        });


    }

    private void initTabelView(){
        TableColumn nameCol = new TableColumn("Nom");
        nameCol.setCellValueFactory(new PropertyValueFactory<Course, String>("name"));
        nameCol.prefWidthProperty().bind(tbListCourse.widthProperty().divide(3));
        TableColumn kmCol = new TableColumn("Km");
        kmCol.setCellValueFactory(new PropertyValueFactory<Course, Double>("km"));
        kmCol.prefWidthProperty().bind(tbListCourse.widthProperty().divide(3));
        TableColumn numberOfTurnsCol = new TableColumn("Tours");
        numberOfTurnsCol.setCellValueFactory(new PropertyValueFactory<Course, Integer>("numberOfTurns"));
        numberOfTurnsCol.prefWidthProperty().bind(tbListCourse.widthProperty().divide(3));

        tbListCourse.getColumns().addAll(nameCol, kmCol, numberOfTurnsCol);

        tbListCourseData = FXCollections.observableArrayList();
        tbListCourse.setItems(tbListCourseData);
    }

    private void refreshTableViewData(){
        tbListCourseData.clear();
        for (Course course : currentRace.getCourses()) {
            tbListCourseData.add(course);
        }
        tbListCourse.refresh();
    }

    private void getCurrentRace() {
        try {
            currentRace = raceRepository.find(this.currentRace.getId());
        } catch (DatabaseException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Problème avec la base de données");
            alert.setHeaderText("Une erreur est survenue lors de l'accès à la base de données");
            alert.setContentText("Impossible de récupérer la course pour le moment.");

            ButtonType refresh = new ButtonType("Réessayer", ButtonBar.ButtonData.OK_DONE);

            alert.getButtonTypes().setAll(refresh);
            Optional<ButtonType> result = alert.showAndWait();


            if (result.get() == refresh) {
                getCurrentRace();
            }
        }
    }

    public void onClickBtnCreateRace(Event event) {
        if (txtNameOfRace.getText() == null) {
            txtNameOfRace.setStyle("-fx-border-color: red");
            return;
        } else {
            txtNameOfRace.setStyle("-fx-border-color: transparent");
        }
        if (tbListCourseData.isEmpty()) {
            tbListCourse.setStyle("-fx-border-color: red");
            return;
        } else {
            tbListCourse.setStyle("-fx-border-color: transparent");
        }

        currentRace.setName(txtNameOfRace.getText());
        updateCurrentRace();
        ((Stage) btnCreateCourse.getScene().getWindow()).close();
        try {
            Stage stage = new Stage();
            stage.setUserData(currentRace);
            (new HomeView()).start(stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateCurrentRace(){
        try {
            raceRepository.merge(currentRace);
        } catch (DatabaseException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Problème avec la base de données");
            alert.setHeaderText("Une erreur est survenue lors de l'accès à la base de données");
            alert.setContentText("Impossible de mettre à jour la course pour le moment.");

            ButtonType refresh = new ButtonType("Réessayer", ButtonBar.ButtonData.OK_DONE);

            alert.getButtonTypes().setAll(refresh);
            Optional<ButtonType> result = alert.showAndWait();


            if (result.get() == refresh) {
                updateCurrentRace();
            }
        }

    }

    public void onClickBtnCreateCourse(Event event) {
        boolean canCreate = true;
        if (txtCourseName.getText().equals("")) {
            txtCourseName.setStyle("-fx-border-color: red");
            canCreate = false;
        } else {
            txtCourseName.setStyle("-fx-border: none");
        }
        if (txtCourseKm.getText().equals("")) {
            txtCourseKm.setStyle("-fx-border-color: red");
            canCreate = false;
        } else {
            txtCourseKm.setStyle("-fx-border: none");
        }
        if (txtCourseNumberOfTurns.getText().equals("")) {
            txtCourseNumberOfTurns.setStyle("-fx-border-color: red");
            canCreate = false;
        } else {
            txtCourseNumberOfTurns.setStyle("-fx-border: none");
        }
        try {
            if (Float.parseFloat(txtCourseKm.getText()) <= 0) {
                throw new NumberFormatException();
            }
            txtCourseKm.setStyle("-fx-border: none");
        } catch (NumberFormatException e) {
            txtCourseKm.setStyle("-fx-border-color: red");
            canCreate = false;
        }
        try {
            if (Integer.parseInt(txtCourseNumberOfTurns.getText()) <= 0) {
                throw new NumberFormatException();
            }
            txtCourseNumberOfTurns.setStyle("-fx-border: none");
        } catch (NumberFormatException e) {
            txtCourseNumberOfTurns.setStyle("-fx-border-color: red");
            canCreate = false;
        }

        if (canCreate) {
            Course course = new Course(txtCourseName.getText(), Float.parseFloat(txtCourseKm.getText()), Integer.parseInt(txtCourseNumberOfTurns.getText()));
            currentRace.getCourses().add(course);
            course.setRace(currentRace);
            tbListCourseData.add(course);
            txtCourseName.setText("");
            txtCourseKm.setText("");
            txtCourseNumberOfTurns.setText("");
            txtCourseName.requestFocus();
        }
    }

    public void onClickBtnRemoveCourse(Event event) {
        Course course = (Course) tbListCourse.getSelectionModel().getSelectedItem();
        if (course != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Vous êtes sur le points de supprimer un parcours !");
            alert.setContentText("Le parcours que vous souhaitez supprimer contient " + course.getTeams().size() + " équipe" + ((course.getTeams().size() > 1) ? "s" : "") + ". Souhaitez-vous le supprimer ?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                currentRace.getCourses().remove(course);
                tbListCourse.getItems().remove(course);
            } else {
                // ... user chose CANCEL or closed the dialog
            }
        }
    }

    public void onActionTxtCourseName(Event event) {
        onClickBtnCreateCourse(event);
    }

    public void onActionTxtCourseKm(Event event) {
        onClickBtnCreateCourse(event);
    }

    public void onActionTxtCourseNumberOfTurns(Event event) {
        onClickBtnCreateCourse(event);
    }

    public void onclickOnItemTbListCourse(Event event) {
        Stage stage = new Stage();
        stage.setUserData(tbListCourse.getSelectionModel().getSelectedItem());
        new CategoryView().start(stage);
    }

}
