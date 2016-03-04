package be.bewweb.StopWatch.Controler;

import be.bewweb.StopWatch.Modele.Course;
import be.bewweb.StopWatch.Modele.Listener.CourseListener;
import be.bewweb.StopWatch.Modele.Listener.RaceListener;
import be.bewweb.StopWatch.Modele.Listener.RunnerListener;
import be.bewweb.StopWatch.Modele.Listener.TeamListener;
import be.bewweb.StopWatch.Modele.Race;
import be.bewweb.StopWatch.Modele.Runner;
import be.bewweb.StopWatch.Modele.Team;
import be.bewweb.StopWatch.Utils.GenerateRanking;
import be.bewweb.StopWatch.View.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class homeController extends baseController {
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
    private TableView tbListTeam;
    private ObservableList<Team> tbListTeamData;
    @FXML
    private MenuItem menuItemModifyRace;
    @FXML
    private MenuItem menuItemGenerateAllRanking;
    @FXML
    private Label lblNameRace;

    @FXML
    private TabPane tpCourse;


    private RaceListener raceListener;

    @FXML
    public void initialize(){

        //Event
        btnAdd.setOnAction(event -> onClickBtnAdd(event));
        btnRemove.setOnAction(event -> onClickBtnRemove(event));
        btnShow.setOnAction(event -> onClickBtnShow(event));
        btnEncode.setOnAction(event -> onClickBtnEncode(event));
        btnShowRanking.setOnAction(event -> onClickBtnShowRanking(event));
        menuItemModifyRace.setOnAction(event -> onClickMenuItemModifyRace(event));
        menuItemGenerateAllRanking.setOnAction(event -> onClickMenuItemGenerateAllRanking(event));
        tbListTeam.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                    if(mouseEvent.getClickCount() == 2){
                        onClickBtnShow(mouseEvent);
                    }
                }
            }
        });

        initRace();
        initCourses();
        initTableView();
        setContentInTableView();
        initTabPane();

    }

    private void initRace(){
        raceListener = new RaceListener() {
            @Override
            public void nameChanged(String name) {
                lblNameRace.setText(name);
            }

            @Override
            public void pathChanged(String path) {

            }

            @Override
            public void courseAdded(Course course) {
                tbListTeamData.addAll(course.getTeams());
                initTabPane();
            }

            @Override
            public void courseRemoved(Course course) {
                tbListTeamData.removeAll(course.getTeams());
                initTabPane();
            }
        };
        Race.getInstance().addListener(raceListener);
        lblNameRace.setText(Race.getInstance().getName());
    }
    public void initCourses(){
        for (Course course : Race.getInstance().getCourses()){
            course.addListener(new CourseListener() {
                @Override
                public void nameChanged(String name) {}

                @Override
                public void kmChanged(float km) {}

                @Override
                public void teamAdded(Team team) {
                    tbListTeamData.add(team);
                }

                @Override
                public void teamRemoved(Team team) {
                    tbListTeamData.remove(team);
                }

            });
        }
    }
    private void initTableView(){
        TableColumn dossardCol = new TableColumn("Dos");
        dossardCol.setCellValueFactory(new PropertyValueFactory<Team, String>("dossard"));
        dossardCol.prefWidthProperty().bind(tbListTeam.widthProperty().divide(4));

        TableColumn runner1Col = new TableColumn("Runner 1");
        runner1Col.setCellValueFactory(new PropertyValueFactory<Team, String>("runner1"));
        dossardCol.prefWidthProperty().bind(tbListTeam.widthProperty().divide(4));

        TableColumn runner2Col = new TableColumn("Runner 2");
        runner2Col.setCellValueFactory(new PropertyValueFactory<Team, String>("runner2"));
        runner2Col.prefWidthProperty().bind(tbListTeam.widthProperty().divide(4));

        TableColumn registrationValidatedCol = new TableColumn("Inscription validé");
        registrationValidatedCol.setCellValueFactory(new PropertyValueFactory<Team, String>("registrationValidated"));
        registrationValidatedCol.prefWidthProperty().bind(tbListTeam.widthProperty().divide(4));

        tbListTeam.getColumns().addAll(dossardCol, runner1Col, runner2Col, registrationValidatedCol);
    }
    private void setContentInTableView(){
        tbListTeamData = FXCollections.observableArrayList();
        for (Course course : Race.getInstance().getCourses()){
            for(Team team: course.getTeams()) {

                team.addListener(new TeamListener() {
                    @Override
                    public void dossardChanged(int dossard) {
                        tbListTeam.refresh();
                    }

                    @Override
                    public void startTimeChanged(long start) {
                        tbListTeam.refresh();
                    }

                    @Override
                    public void endTimeChanged(ArrayList<Long> end) {
                        tbListTeam.refresh();
                    }


                    @Override
                    public void runner1Changed(Runner runner) {
                        tbListTeam.refresh();
                    }

                    @Override
                    public void runner2Changed(Runner runner) {
                        tbListTeam.refresh();
                    }

                    @Override
                    public void registrationValidatedChanged(Boolean registrationValidated) {
                        tbListTeam.refresh();
                    }
                });

                try{
                    team.getRunner1().addListener(new RunnerListener() {
                        @Override
                        public void nameChanged(String name) {
                            tbListTeam.refresh();
                        }

                        @Override
                        public void firstnameChanged(String firstname) {
                            tbListTeam.refresh();
                        }

                        @Override
                        public void sexChanged(boolean isMan) {
                            tbListTeam.refresh();
                        }

                        @Override
                        public void birthDateChanged(Date birthDate) {
                            tbListTeam.refresh();
                        }
                    });
                }catch (NullPointerException e){
                    //Runner1 = null
                }
                try{
                    team.getRunner2().addListener(new RunnerListener() {
                        @Override
                        public void nameChanged(String name) {
                            tbListTeam.refresh();
                        }

                        @Override
                        public void firstnameChanged(String firstname) {
                            tbListTeam.refresh();
                        }

                        @Override
                        public void sexChanged(boolean isMan) {
                            tbListTeam.refresh();
                        }

                        @Override
                        public void birthDateChanged(Date birthDate) {
                            tbListTeam.refresh();
                        }
                    });
                }catch (NullPointerException e){
                    //Runner2 = null
                }

                tbListTeamData.add(team);
            }
        }
        tbListTeam.setItems(tbListTeamData);
    }
    private void initTabPane(){
        tpCourse.getTabs().removeAll();
        for (Course course : Race.getInstance().getCourses()){
            try{
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/be/bewweb/StopWatch/View/home/detailsCourse.fxml"));
                fxmlLoader.setController(new detailsCourseController(course));
                Tab tab = new Tab();
                tab.setText(course.toString());
                tab.setContent(fxmlLoader.load());
                tpCourse.getTabs().add(tab);
            }catch (IOException e){
                e.printStackTrace();
                //Do nothing
            }
        }
    }

    public void onClickBtnAdd(Event event){
        new ManageTeamView().start(new Stage());
    }
    public void onClickBtnRemove(Event event){
        Team team = (Team) tbListTeam.getSelectionModel().getSelectedItem();
        if(team != null){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Vous êtes sur le points de supprimer une équipe !");
            alert.setContentText("Attention, cette équipe sera définitivement supprimée. Souhaitez-vous la supprimer ?" );
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){
                for(Course course:Race.getInstance().getCourses()){
                    course.removeTeam(team);
                }
                Race.getInstance().save();
            } else {
                // ... user chose CANCEL or closed the dialog
            }
        }
    }
    public void onClickBtnShow(Event event){
        try{
            Stage stage = new Stage();
            stage.setUserData(tbListTeam.getSelectionModel().getSelectedItem());
            new ManageTeamView().start(stage);
        }catch (NullPointerException e){
            e.printStackTrace();
            //Not item selected
        }
    }
    public void onClickBtnEncode(Event event){
        new EncodeView().start(new Stage());
    }
    public void onClickBtnShowRanking(Event event){
        new RankingView().start(new Stage());
    }

    public void onClickMenuItemModifyRace(Event event){
        ((Stage) btnAdd.getScene().getWindow()).close();
        new parametersRaceView().start(new Stage());
    }
    public void onClickMenuItemGenerateAllRanking(Event event){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File dirSelected = directoryChooser.showDialog(new Stage());
        if (dirSelected != null) {
            generateAllRanking(dirSelected.getAbsolutePath());
        }
    }

    private void generateAllRanking(String pathDir){
        for (Course course: Race.getInstance().getCourses()) {
            new GenerateRanking(course).generate(pathDir);
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Classement généré");
        alert.setHeaderText("Classement généré !");
        alert.setContentText("Tous les classements ont été généré. Souhaitez-vous ouvrir le dossier dans l'explorateur ?" );
        alert.getButtonTypes().remove(ButtonType.OK);
        alert.getButtonTypes().remove(ButtonType.CANCEL);
        alert.getButtonTypes().add(ButtonType.YES);
        alert.getButtonTypes().add(ButtonType.NO);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.YES){
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
