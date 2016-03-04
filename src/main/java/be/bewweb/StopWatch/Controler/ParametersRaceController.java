package be.bewweb.StopWatch.Controler;

import be.bewweb.StopWatch.Modele.Course;
import be.bewweb.StopWatch.Modele.Listener.CourseListener;
import be.bewweb.StopWatch.Modele.Race;
import be.bewweb.StopWatch.Modele.Team;
import be.bewweb.StopWatch.View.CategoryView;
import be.bewweb.StopWatch.View.home.homeView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.Optional;

/**
 * Created by Quentin on 06-02-16.
 */
public class ParametersRaceController extends baseController {

    @FXML
    private Button btnChooseFile;
    @FXML
    private Button btnCreateRace;
    @FXML
    private Button btnCreateCourse;
    @FXML
    private Button btnRemoveCourse;
    @FXML
    private TextField txtPathToRace;
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


    public void initialize(){
        btnChooseFile.setOnAction(event -> onClickBtnChooseFile(event));
        btnCreateRace.setOnAction(event -> onClickBtnCreateRace(event));
        btnCreateCourse.setOnAction(event -> onClickBtnCreateCourse(event));
        btnRemoveCourse.setOnAction(event -> onClickBtnRemoveCourse(event));
        txtCourseName.setOnAction(event -> onActionTxtCourseName(event));
        txtCourseKm.setOnAction(event -> onActionTxtCourseKm(event));
        txtCourseNumberOfTurns.setOnAction(event -> onActionTxtCourseNumberOfTurns(event));
        tbListCourse.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                    if(mouseEvent.getClickCount() == 2){
                        onclickOnItemTbListCourse(mouseEvent);
                    }
                }
            }
        });


        txtNameOfRace.setText(Race.getInstance().getName());
        txtPathToRace.setText(Race.getInstance().getPath());

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
        for (Course course: Race.getInstance().getCourses()) {
            tbListCourseData.add(course);
            refreshListWhenSomethingChange(course);
        }
        tbListCourse.setItems(tbListCourseData);
    }

    private void refreshListWhenSomethingChange(Course course){
        course.addListener(new CourseListener() {
            @Override
            public void nameChanged(java.lang.String name) {
                tbListCourse.refresh();
            }

            @Override
            public void kmChanged(float km) {
                tbListCourse.refresh();
            }

            @Override
            public void teamAdded(Team team) {
                tbListCourse.refresh();
            }

            @Override
            public void teamRemoved(Team team) {
                tbListCourse.refresh();
            }
        });
    }

    public void onClickBtnChooseFile(Event event){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File dirSelected = directoryChooser.showDialog(new Stage());
        if (dirSelected != null) {
            txtPathToRace.setText(dirSelected.toString());
        }
    }
    public void onClickBtnCreateRace(Event event){
        if(txtNameOfRace.getText() == null){
            txtNameOfRace.setStyle("-fx-border-color: red");
            return;
        }else{
            txtNameOfRace.setStyle("-fx-border-color: transparent");
        }
        if(txtPathToRace.getText() == null){
            txtPathToRace.setStyle("-fx-border-color: red");
            return;
        }else{
            txtNameOfRace.setStyle("-fx-border-color: transparent");
        }
        if(tbListCourseData.isEmpty()){
            tbListCourse.setStyle("-fx-border-color: red");
            return;
        }else{
            tbListCourse.setStyle("-fx-border-color: transparent");
        }

        Race.getInstance().setName(txtNameOfRace.getText());
        Race.getInstance().setPath(txtPathToRace.getText());
        Race.getInstance().save();
        ((Stage) btnCreateCourse.getScene().getWindow()).close();
        try {
            (new homeView()).start(new Stage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onClickBtnCreateCourse(Event event){
        boolean canCreate = true;
        if(txtCourseName.getText().equals("")){
            txtCourseName.setStyle("-fx-border-color: red");
            canCreate = false;
        }else{
            txtCourseName.setStyle("-fx-border: none");
        }
        if(txtCourseKm.getText().equals("")){
            txtCourseKm.setStyle("-fx-border-color: red");
            canCreate = false;
        }else{
            txtCourseKm.setStyle("-fx-border: none");
        }
        if(txtCourseNumberOfTurns.getText().equals("")){
            txtCourseNumberOfTurns.setStyle("-fx-border-color: red");
            canCreate = false;
        }else{
            txtCourseNumberOfTurns.setStyle("-fx-border: none");
        }
        try{
            if(Float.parseFloat(txtCourseKm.getText()) <= 0){
                throw new  NumberFormatException();
            }
            txtCourseKm.setStyle("-fx-border: none");
        }catch (NumberFormatException e){
            txtCourseKm.setStyle("-fx-border-color: red");
            canCreate = false;
        }
        try{
            if(Integer.parseInt(txtCourseNumberOfTurns.getText()) <= 0){
                throw new  NumberFormatException();
            }
            txtCourseNumberOfTurns.setStyle("-fx-border: none");
        }catch (NumberFormatException e){
            txtCourseNumberOfTurns.setStyle("-fx-border-color: red");
            canCreate = false;
        }

        if(canCreate){
            Course course = new Course(txtCourseName.getText(), Float.parseFloat(txtCourseKm.getText()), Integer.parseInt(txtCourseNumberOfTurns.getText()));
            Race.getInstance().addCourse(course);
            tbListCourseData.add(course);
            refreshListWhenSomethingChange(course);
            txtCourseName.setText("");
            txtCourseKm.setText("");
            txtCourseNumberOfTurns.setText("");
            txtCourseName.requestFocus();
        }
    }
    public void onClickBtnRemoveCourse(Event event){
        Course course = (Course) tbListCourse.getSelectionModel().getSelectedItem();
        if(course != null){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Vous êtes sur le points de supprimer un parcours !");
            alert.setContentText("Le parcours que vous souhaitez supprimer contient " + course.getTeams().size() + " équipe"+ ((course.getTeams().size() > 1)? "s" : "") +". Souhaitez-vous le supprimer ?" );
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){
                Race.getInstance().removeCourse(course);
                tbListCourse.getItems().remove(course);
            } else {
                // ... user chose CANCEL or closed the dialog
            }
        }
    }

    public void onActionTxtCourseName(Event event){
        onClickBtnCreateCourse(event);
    }
    public void onActionTxtCourseKm(Event event){
        onClickBtnCreateCourse(event);
    }
    public void onActionTxtCourseNumberOfTurns(Event event){
        onClickBtnCreateCourse(event);
    }


    public void onclickOnItemTbListCourse(Event event){
            Stage stage = new Stage();
            stage.setUserData(tbListCourse.getSelectionModel().getSelectedItem());
            new CategoryView().start(stage);
    }

}
