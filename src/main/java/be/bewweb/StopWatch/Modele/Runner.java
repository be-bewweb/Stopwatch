package be.bewweb.StopWatch.Modele;

import be.bewweb.StopWatch.Modele.Listener.RunnerListener;
import org.apache.commons.lang3.text.WordUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Quentin on 06-02-16.
 */
public class Runner  implements Serializable {
    private String name;
    private String firstname;
    private boolean man;
    private Date birthDate;
    private transient ArrayList<RunnerListener> runnerListeners;

    public String getName() {
        return WordUtils.capitalizeFully(name);
    }

    public void setName(String name) {
        if(this.name != WordUtils.capitalizeFully(name)){
            this.name = WordUtils.capitalizeFully(name);
            fireNameChanged();
        }
    }

    public String getFirstname() {
        return WordUtils.capitalizeFully(firstname);
    }

    public void setFirstname(String firstname) {
        if(this.firstname != WordUtils.capitalizeFully(firstname)){
            this.firstname = WordUtils.capitalizeFully(firstname);
            firefirstnameChanged();
        }
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        if(this.birthDate != birthDate){
            this.birthDate = birthDate;
            fireBirthDateChanged();
        }
    }

    public boolean isMan(){
        return this.man;
    }
    public void setMan(boolean man){
        if(this.man != man){
            this.man = man;
            fireSexChanged();
        }
    }


    public static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }
    public int getAge(){
        Calendar a = getCalendar(birthDate);
        Calendar b = getCalendar(new Date());
        int diff = b.get(Calendar.YEAR) - a.get(Calendar.YEAR);
        if (a.get(Calendar.MONTH) > b.get(Calendar.MONTH) ||
                (a.get(Calendar.MONTH) == b.get(Calendar.MONTH) && a.get(Calendar.DATE) > b.get(Calendar.DATE))) {
            diff--;
        }
        return diff;
    }

    @Override
    public String toString() {
        return this.name.toUpperCase() + " " + this.firstname;
    }


    //Listener
    public void addListener(RunnerListener runnerListener) {
        if(this.runnerListeners == null){
            this.runnerListeners = new ArrayList<>();
        }
        this.runnerListeners.add(runnerListener);
    }
    public boolean removeListener(RunnerListener runnerListener) {
        return this.runnerListeners.remove(runnerListener);
    }
    private void fireNameChanged(){
        if(this.runnerListeners != null){
            ArrayList<RunnerListener> runnerListeners = (ArrayList) this.runnerListeners.clone();
            for (RunnerListener listener : runnerListeners) {
                listener.nameChanged(this.name);
            }
        }
    }
    private void firefirstnameChanged(){
        if(this.runnerListeners != null){
            ArrayList<RunnerListener> runnerListeners = (ArrayList) this.runnerListeners.clone();
            for (RunnerListener listener : runnerListeners) {
                listener.firstnameChanged(this.firstname);
            }
        }
    }
    private void fireSexChanged(){
        if(this.runnerListeners != null){
            ArrayList<RunnerListener> runnerListeners = (ArrayList) this.runnerListeners.clone();
            for (RunnerListener listener : runnerListeners) {
                listener.sexChanged(this.man);
            }
        }
    }
    private void fireBirthDateChanged(){
        if(this.runnerListeners != null){
            ArrayList<RunnerListener> runnerListeners = (ArrayList) this.runnerListeners.clone();
            for (RunnerListener listener : runnerListeners) {
                listener.birthDateChanged(this.birthDate);
            }
        }
    }

}
