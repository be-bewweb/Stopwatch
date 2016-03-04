package be.bewweb.StopWatch.Controler;

import be.bewweb.StopWatch.Modele.Course;
import be.bewweb.StopWatch.Modele.Race;
import be.bewweb.StopWatch.Modele.Team;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

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
        timeCol.setCellValueFactory(new PropertyValueFactory<TeamLog, String>("timeStr"));
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

    private void initTableViewValue() {
        for (Course course : Race.getInstance().getCourses()) {
            for (Team team : course.getTeams()) {
                int turn = 1;
                for (long time : team.getEndTime()) {
                    tbListTeamLogData.add(new TeamLog(team.getDossard(), team.getRunner1().toString(), team.getRunner2().toString(), turn, course.getNumberOfTurns(), time - team.getStartTime()));
                    turn++;
                }
            }
        }
        tbListTeamLogData.sort((o1, o2) -> (int) (o1.getTime() - o2.getTime()));
    }

    private void onClickBtnArrived(Event event) {
        if (txtDossard.getText().equals("")) {
            txtDossard.setStyle("-fx-background-color: red");
            txtDossard.requestFocus();
            lblInfo.setText("Numéro de dossard nécessaire");
            return;
        }

        boolean found = false;
        for (Course course : Race.getInstance().getCourses()) {
            for (Team team : course.getTeams()) {
                if (course.getNumberOfTurns() > team.getEndTime().size()) {
                    try {
                        if (team.getDossard() == Integer.parseInt(txtDossard.getText())) {

                            if (!team.isRegistrationValidated()) {
                                lblInfo.setText("Inscription non validé");
                                return;
                            }

                            try {
                                DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
                                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                                Date date = sdf.parse(sdf.format(new Date()));
                                long timestampUTC = getTimestamp(sdf.format(date), "dd/MM/yyyy HH:mm:ss.SSS");
                                team.addEndTime(timestampUTC);

                                txtDossard.setStyle("-fx-background-color: green");
                                txtDossard.setText("");
                                lblInfo.setText("");
                                txtDossard.requestFocus();

                                tbListTeamLogData.addAll(new TeamLog(team.getDossard(), team.getRunner1().toString(), team.getRunner2().toString(), team.getEndTime().size(), course.getNumberOfTurns(), date.getTime() - team.getStartTime()));

                                Race.getInstance().save();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            return;
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        if (!found) {
            txtDossard.setText("");
            txtDossard.setStyle("-fx-background-color: orange");
            txtDossard.requestFocus();
            return;
        }

    }

    private void onKeyPressedTxtDossard(Event event) {
        txtDossard.setStyle("-fx-background-color: transparent");
    }

    private void onActionTxtDossard(Event event) {
        onClickBtnArrived(event);
    }

    public class TeamLog {
        private int dossard;
        private String runner1;
        private String runner2;
        private int turn;
        private int turnTot;
        private long time;

        public TeamLog(int dossard, String runner1, String runner2, int turn, int turnTot, long time) {
            this.dossard = dossard;
            this.runner1 = runner1;
            this.runner2 = runner2;
            this.turn = turn;
            this.turnTot = turnTot;
            this.time = time;
        }

        public int getDossard() {
            return dossard;
        }

        public String getRunner1() {
            return runner1;
        }

        public String getRunner2() {
            return runner2;
        }

        public String getTurnStr() {
            return this.turn + "/" + this.turnTot;
        }

        public String getTimeStr() {
            DateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            return sdf.format(time);
        }

        public long getTime() {
            return time;
        }
    }

}
