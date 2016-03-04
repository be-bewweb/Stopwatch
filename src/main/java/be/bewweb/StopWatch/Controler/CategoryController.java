package be.bewweb.StopWatch.Controler;

import be.bewweb.StopWatch.Modele.Category;
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

public class CategoryController extends baseController {

    @FXML
    private Label lblIntervalAdult;
    @FXML
    private Label lblIntervalChild;
    @FXML
    private Label lblIntervalYoung;
    @FXML
    private Label lblIntervalSenior;
    @FXML
    private Label lblIntervalVeteranA;
    @FXML
    private Label lblIntervalVeteranB;
    @FXML
    private Label lblNameCourse;
    @FXML
    private TextField txtCourseNumberOfTurns;
    @FXML
    private TextField txtCourseName;
    @FXML
    private TextField txtCourseKm;


    @FXML
    private Label lblNumberFamilyA;
    @FXML
    private Label lblNumberFamilyB;
    @FXML
    private Label lblNumberYoungA;
    @FXML
    private Label lblNumberYoungB;
    @FXML
    private Label lblNumberYoungC;
    @FXML
    private Label lblNumberAdult;

    @FXML
    private Label lblNumberYoung;
    @FXML
    private Label lblNumberSenior;
    @FXML
    private Label lblNumberVeteranA;
    @FXML
    private Label lblNumberVeteranB;
    @FXML
    private Label lblNumberWomen;
    @FXML
    private Label lblNumberMixed;


    @FXML
    private Slider sliderAdult;
    @FXML
    private Slider sliderChild;
    @FXML
    private Slider sliderYoung;
    @FXML
    private Slider sliderSenior;
    @FXML
    private Slider sliderVeteranA;

    @FXML
    private Button btnReset;
    @FXML
    private Button btnSave;

    private Course currentCourse;

    @FXML
    public void initialize(){
        sliderAdult.valueProperty().addListener((observable, oldValue, newValue) -> onValueChangedSliderAdult(newValue));
        sliderChild.valueProperty().addListener((observable, oldValue, newValue) -> onValueChangedSliderChild(newValue));
        sliderYoung.valueProperty().addListener((observable, oldValue, newValue) -> onValueChangedSliderYoung(newValue));
        sliderSenior.valueProperty().addListener((observable, oldValue, newValue) -> onValueChangedSliderSenior(newValue));
        sliderVeteranA.valueProperty().addListener((observable, oldValue, newValue) -> onValueChangedSliderVeteranA(newValue));
        btnReset.setOnAction(event -> onClickBtnReset(event));
        btnSave.setOnAction(event -> onClickBtnSave(event));

    }

    @Override
    public void initialized(){
        currentCourse = (Course) getStage().getUserData();
        lblNameCourse.setText(currentCourse.toString());
        initCategory();
        updateNumberOfTeamsPerCategory();

        txtCourseKm.setText(currentCourse.getKm()+"");
        txtCourseName.setText(currentCourse.getName());
        txtCourseNumberOfTurns.setText(currentCourse.getNumberOfTurns()+"");
    }

    private void initCategory(){
        sliderAdult.setValue(currentCourse.getCategory().getAdult());
        sliderChild.setValue(currentCourse.getCategory().getChild());
        sliderYoung.setValue(currentCourse.getCategory().getYoung());
        sliderSenior.setValue(currentCourse.getCategory().getSenior());
        sliderVeteranA.setValue(currentCourse.getCategory().getVeteranA());
    }
    private String formatInteger(int i){
        if(Math.abs(i) < 10){
            return "00" + i;
        }
        if(Math.abs(i) < 100){
            return "0" + i;
        }
        return i+"";
    }
    private void updateNumberOfTeamsPerCategory(){
        int familyA = 0;
        int familyB = 0;
        int youngA = 0;
        int youngB = 0;
        int youngC = 0;
        int adult = 0;

        int young = 0;
        int senior = 0;
        int veteranA = 0;
        int veteranB = 0;
        int women = 0;
        int mixed = 0;

        int sAdult =  (int) sliderAdult.getValue();
        int sChild =  (int) sliderChild.getValue();

        int sYoung =  (int) sliderYoung.getValue();
        int sSenior =  (int) sliderSenior.getValue();
        int sVeteranA =  (int) sliderVeteranA.getValue();

        for(Team team : currentCourse.getTeams()){
            //Familles A : A moins de 12 ans - B plus de 16 ans.
            if(team.getRunner1().getAge() < sChild && team.getRunner2().getAge() > sAdult){
                familyA++;
            }
            if(team.getRunner2().getAge() < sChild && team.getRunner1().getAge() > sAdult){
                familyA++;
            }

            //Familles B : A entre 12 et 16 ans - B plus de 16 ans.
            if(team.getRunner1().getAge() >= sChild && team.getRunner1().getAge() <= sAdult && team.getRunner2().getAge() > sAdult){
                familyB++;
            }
            if(team.getRunner2().getAge() >= sChild && team.getRunner2().getAge() <= sAdult && team.getRunner1().getAge() > sAdult){
                familyB++;
            }

            //Jeunes A : A et B moins de 12 ans.
            if(team.getRunner1().getAge() < sChild && team.getRunner2().getAge() < sChild){
                youngA++;
            }

            //Jeunes B : A moins de 12 - B entre 12 et 16 ans.
            if(team.getRunner1().getAge() >= sChild && team.getRunner1().getAge() <= sAdult && team.getRunner2().getAge() < sChild){
                youngB++;
            }
            if(team.getRunner2().getAge() >= sChild && team.getRunner2().getAge() <= sAdult && team.getRunner1().getAge() < sChild){
                youngB++;
            }

            //Jeunes C : A et B entre 12 et 16 ans.
            if(team.getRunner1().getAge() >= sChild && team.getRunner1().getAge() <= sAdult && team.getRunner2().getAge() >= sChild && team.getRunner2().getAge() <= sAdult){
                youngC++;
            }

            //Adultes : les équipes dont les 2 ont plus de 16 ans seront reprises dans un classement à part et seront récompensées.
            if(team.getRunner1().getAge() > sAdult && team.getRunner2().getAge() > sAdult){
                adult++;
            }

            //Un classement général avec mention des catégories (moyenne d’âge des deux équipiers).
            float avrAge = ((float) team.getRunner1().getAge() + team.getRunner2().getAge()) / ((float)2);

            //Jeunes : moins de 21 ans
            if(avrAge < sYoung){
                young++;
            }

            //Seniors : plus ou égal à 21 ans et moins de 40 ans
            if(avrAge >= sYoung && avrAge < sSenior){
                senior++;
            }

            //Vétérans A : plus ou égal à 40 ans et moins de 50 ans
            if(avrAge >= sSenior && avrAge < sVeteranA){
                veteranA++;
            }
            //Vétérans B : plus de 50 ans
            if(avrAge >= sVeteranA){
                veteranB++;
            }

            //Dame
            if(!team.getRunner1().isMan() && !team.getRunner2().isMan()){
                women++;
            }

            //Mixte
            if(!team.getRunner1().isMan() && team.getRunner2().isMan()){
                mixed++;
            }
            if(team.getRunner1().isMan() && !team.getRunner2().isMan()){
                mixed++;
            }
        }

        lblNumberFamilyA.setText(formatInteger(familyA));
        lblNumberFamilyB.setText(formatInteger(familyB));
        lblNumberYoungA.setText(formatInteger(youngA));
        lblNumberYoungB.setText(formatInteger(youngB));
        lblNumberYoungC.setText(formatInteger(youngC));
        lblNumberAdult.setText(formatInteger(adult));

        lblNumberYoung.setText(formatInteger(young));
        lblNumberSenior.setText(formatInteger(senior));
        lblNumberVeteranA.setText(formatInteger(veteranA));
        lblNumberVeteranB.setText(formatInteger(veteranB));
        lblNumberWomen.setText(formatInteger(women));
        lblNumberMixed.setText(formatInteger(mixed));

    }

    private void onClickBtnReset(Event event){
        Category defaultCat = new Category();
        sliderAdult.setValue(defaultCat.getAdult());
        sliderChild.setValue(defaultCat.getChild());
        sliderYoung.setValue(defaultCat.getYoung());
        sliderSenior.setValue(defaultCat.getSenior());
        sliderVeteranA.setValue(defaultCat.getVeteranA());

        txtCourseKm.setText(currentCourse.getKm()+"");
        txtCourseName.setText(currentCourse.getName());
        txtCourseNumberOfTurns.setText(currentCourse.getNumberOfTurns()+"");

    }
    private void onClickBtnSave(Event event){
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

        if(canCreate) {

            currentCourse.getCategory().setAdult((int) sliderAdult.getValue());
            currentCourse.getCategory().setChild((int) sliderChild.getValue());
            currentCourse.getCategory().setYoung((int) sliderYoung.getValue());
            currentCourse.getCategory().setSenior((int) sliderSenior.getValue());
            currentCourse.getCategory().setVeteranA((int) sliderVeteranA.getValue());

            currentCourse.setName(txtCourseName.getText());
            currentCourse.setKm(Float.parseFloat(txtCourseKm.getText()));
            currentCourse.setNumberOfTurns(Integer.parseInt(txtCourseNumberOfTurns.getText()));

            Race.getInstance().save();
            getStage().close();
        }
    }

    private void onValueChangedSliderAdult(Number value){
        int newValue = value.intValue();
        if(value.doubleValue() < sliderChild.getValue()){
            sliderChild.setValue(value.doubleValue());
        }
        sliderAdult.setValue(newValue);
        lblIntervalAdult.setText("]"+newValue+" -  ]");
        updateNumberOfTeamsPerCategory();
    }
    private void onValueChangedSliderChild(Number value){
        int newValue = value.intValue();
        if(value.doubleValue() > sliderAdult.getValue()){
            sliderAdult.setValue(value.doubleValue());
        }
        sliderChild.setValue(newValue);
        lblIntervalChild.setText("[0 - "+newValue+"[");
        updateNumberOfTeamsPerCategory();
    }

    private void onValueChangedSliderYoung(Number value){
        int newValue = value.intValue();
        if(value.doubleValue() > sliderSenior.getValue()){
            sliderSenior.setValue(value.doubleValue());
        }
        sliderYoung.setValue(newValue);
        lblIntervalYoung.setText("[0 - "+newValue+"[");
        lblIntervalSenior.setText("["+newValue+" - "+(int) sliderSenior.getValue()+"[");
        updateNumberOfTeamsPerCategory();
    }
    private void onValueChangedSliderSenior(Number value){
        int newValue = value.intValue();
        if(value.doubleValue() > sliderVeteranA.getValue()){
            sliderVeteranA.setValue(value.doubleValue());
        }
        if(value.doubleValue() < sliderYoung.getValue()){
            sliderYoung.setValue(value.doubleValue());
        }
        sliderSenior.setValue(newValue);
        lblIntervalSenior.setText("["+(int) sliderYoung.getValue()+" - "+newValue+"[");
        lblIntervalVeteranA.setText("["+newValue+" - "+(int) sliderVeteranA.getValue()+"[");
        updateNumberOfTeamsPerCategory();
    }
    private void onValueChangedSliderVeteranA(Number value){
        int newValue = value.intValue();
        if(value.doubleValue() < sliderSenior.getValue()){
            sliderSenior.setValue(value.doubleValue());
        }
        sliderVeteranA.setValue(newValue);
        lblIntervalVeteranA.setText("["+(int) sliderSenior.getValue()+" - "+newValue+"[");
        lblIntervalVeteranB.setText("["+newValue+" -  ]");
        updateNumberOfTeamsPerCategory();
    }

}
