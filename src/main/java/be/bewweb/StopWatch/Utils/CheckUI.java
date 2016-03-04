package be.bewweb.StopWatch.Utils;

import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.time.ZoneId;

/**
 * Created by Quentin on 15-02-16.
 */
public class CheckUI {
    public static boolean isNotEmpty(TextField o){
        if(o.getText() == null){
            o.setStyle("-fx-border-color: red");
            return false;
        }else {
            if(o.getText().equals("")){
                o.setStyle("-fx-border-color: red");
                return false;
            }else{
                o.setStyle("-fx-border: none");
                return true;
            }
        }
    }
    public static boolean isNotEmpty(DatePicker o){
        try {
            o.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant();
            o.setStyle("-fx-border: none");
            return true;
        }catch (Exception  e){
            e.printStackTrace();
            o.setStyle("-fx-border-color: red");
            return false;
        }
    }
    public static boolean isNotEmpty(ComboBox o){
        try {
            if(!o.getValue().equals(null)){
                o.setStyle("-fx-border: none");
                return true;
            }else{
                o.setStyle("-fx-border-color: red");
                return false;
            }
        }catch (Exception  e){
            e.printStackTrace();
            o.setStyle("-fx-border-color: red");
            return false;
        }
    }
}
