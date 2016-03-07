package be.bewweb.StopWatch.Controler;

import be.bewweb.StopWatch.Modele.Course;
import be.bewweb.StopWatch.Modele.Race;
import be.bewweb.StopWatch.Modele.Runner;
import be.bewweb.StopWatch.Modele.Team;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static be.bewweb.StopWatch.Utils.Time.getTimestamp;

public class EncodeController extends baseController {

    @FXML
    private Button btnArrived;

    @FXML
    private TextField txtDossard;

    @FXML
    private Label lblInfo;

    @FXML
    private TableView tbListTeamLog;
    private ObservableList<TeamLog> tbListTeamLogData;


    @FXML
    public void initialize() {
        btnArrived.setOnAction(event -> onClickBtnArrived(event));
        txtDossard.setOnKeyPressed(event -> onKeyPressedTxtDossard(event));
        txtDossard.setOnAction(event -> onActionTxtDossard(event));
        btnArrived.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                onClickBtnArrived(event);
            }
        });
        initDeleteItemTableView();
        initTableView();
        initTableViewValue();

    }

    private void initTableView() {
        tbListTeamLogData = FXCollections.observableArrayList();
        TableColumn dossardCol = new TableColumn("Dos");
        dossardCol.setCellValueFactory(new PropertyValueFactory<TeamLog, String>("dossard"));
        dossardCol.setStyle("-fx-alignment: CENTER;");
        dossardCol.prefWidthProperty().bind(tbListTeamLog.widthProperty().divide(5));

        TableColumn runner1Col = new TableColumn("Runner 1");
        runner1Col.setCellValueFactory(new PropertyValueFactory<TeamLog, String>("runner1"));
        runner1Col.setStyle("-fx-alignment: CENTER;");
        runner1Col.prefWidthProperty().bind(tbListTeamLog.widthProperty().divide(5));

        TableColumn runner2Col = new TableColumn("Runner 2");
        runner2Col.setCellValueFactory(new PropertyValueFactory<TeamLog, String>("runner2"));
        runner2Col.setStyle("-fx-alignment: CENTER;");
        runner2Col.prefWidthProperty().bind(tbListTeamLog.widthProperty().divide(5));

        TableColumn turnCol = new TableColumn("Tours");
        turnCol.setCellValueFactory(new PropertyValueFactory<TeamLog, String>("turnStr"));
        turnCol.setStyle("-fx-alignment: CENTER;");
        turnCol.prefWidthProperty().bind(tbListTeamLog.widthProperty().divide(5));

        TableColumn timeCol = new TableColumn("Temps");
        timeCol.setCellValueFactory(new PropertyValueFactory<TeamLog, String>("durationStr"));
        timeCol.setStyle("-fx-alignment: CENTER;");
        timeCol.prefWidthProperty().bind(tbListTeamLog.widthProperty().divide(5));

        tbListTeamLog.getColumns().addAll(dossardCol, runner1Col, runner2Col, turnCol, timeCol);

        tbListTeamLog.setItems(tbListTeamLogData);

        //Scroll down
        tbListTeamLog.getItems().addListener(new ListChangeListener() {
            @Override
            public void onChanged(Change c) {
                final int size = tbListTeamLog.getItems().size();
                if (size > 0) {
                    tbListTeamLog.scrollTo(size - 1);
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
        alert.setContentText("Vous souhaitez supprimer le tour numéro " + teamLog.getTurn() + " de l'équipe portant le dossard " + teamLog.getDossard() + ". Cette action est irréversible !");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            boolean deleted = false;
            tbListTeamLogData.remove(teamLog);
            for (Course course : Race.getInstance().getCourses()) {
                for (Team team : course.getTeams()) {
                    if (team.getDossard() == teamLog.getDossard()) {
                        try {
                            team.removeEndTime(teamLog.getEndTime());
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
            } else {
                tbListTeamLog.refresh();
                Race.getInstance().save();
            }
        } else {
            // ... user chose CANCEL or closed the dialog
        }
    }

    private void initTableViewValue() {
        for (Course course : Race.getInstance().getCourses()) {
            for (Team team : course.getTeams()) {
                int turn = 1;
                for (long endTime : team.getEndTime()) {
                    tbListTeamLogData.add(new TeamLog(team.getDossard(), team.getRunner1(), team.getRunner2(), turn, course.getNumberOfTurns(), team.getTime(turn), endTime));
                    turn++;
                }
            }
        }
        tbListTeamLogData.sort((o1, o2) -> (int) (o1.getEndTime() - o2.getEndTime()));
    }

    private void UIDossard(String message, String color){
        lblInfo.setText(message);
        txtDossard.setText("");
        txtDossard.setStyle("-fx-background-color: "+color);
        txtDossard.requestFocus();
    }

    private void onClickBtnArrived(Event event) {

        if (txtDossard.getText().equals("")) {
            UIDossard("Numéro de dossard nécessaire", "red");
            return;
        }


        try{
            int dossard = Integer.parseInt(txtDossard.getText());
            Team teamFound = null;
            Course courseFound = null;
            boolean maxTurn = false;

            DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = sdf.parse(sdf.format(new Date()));
            long timestampUTC = getTimestamp(sdf.format(date), "dd/MM/yyyy HH:mm:ss.SSS");


            Iterator<Course> courses = Race.getInstance().getCourses().iterator();
            while(courses.hasNext() && teamFound == null){
                Course course = courses.next();
                Iterator<Team> teams = course.getTeams().iterator();
                while (teams.hasNext() && teamFound == null){
                    Team team = teams.next();
                    if(dossard == team.getDossard()){
                            teamFound = team;
                            courseFound = course;
                    }
                }
            }

            if(teamFound == null){
                teamFound = new Team();
                teamFound.setDossard(dossard);
                teamFound.setRegistrationValidated(true);
                courseFound = Race.getInstance().getCourses().get(0);
                courseFound.addTeam(teamFound);
                UIDossard("Dossard introuvable", "green");
            }else {
                if(teamFound.getEndTime().size() >= courseFound.getNumberOfTurns()){
                    UIDossard("Attention, course déjà terminé !", "orange");
                }else{
                    UIDossard("OK", "green");
                }
            }
            teamFound.addEndTime(timestampUTC);
            tbListTeamLogData.add(new TeamLog(teamFound.getDossard(), teamFound.getRunner1(), teamFound.getRunner2(), teamFound.getEndTime().size(), courseFound.getNumberOfTurns(), teamFound.getTime(), teamFound.getEndTime().get(teamFound.getEndTime().size() - 1)));

            Race.getInstance().save();

        }catch (Exception e){
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

    public class TeamLog {
        private int dossard;
        private Runner runner1;
        private Runner runner2;
        private int turn;
        private int turnTot;
        private long duration;
        private long endTime;

        public TeamLog(int dossard, Runner runner1, Runner runner2, int turn, int turnTot, long duration, long endTime) {
            this.dossard = dossard;
            this.runner1 = runner1;
            this.runner2 = runner2;
            this.turn = turn;
            this.turnTot = turnTot;
            this.duration = duration;
            this.endTime = endTime;
        }

        public int getDossard() {
            return dossard;
        }

        public String getRunner1() {
            if(runner1 ==null){
                return "";
            }
            return runner1.toString();
        }

        public String getRunner2() {
            if(runner2 ==null){
                return "";
            }
            return runner2.toString();
        }

        public String getTurnStr() {
            return this.turn + "/" + this.turnTot;
        }

        public int getTurn() {
            return turn;
        }

        public String getDurationStr() {
            DateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            return sdf.format(duration);
        }

        public long getDuration() {
            return duration;
        }

        public long getEndTime() {
            return endTime;
        }

        public void setEndTime(long endTime) {
            this.endTime = endTime;
        }
    }

}
