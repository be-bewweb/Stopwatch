package be.bewweb.StopWatch.Modele;

import be.bewweb.StopWatch.Modele.Listener.CourseListener;
import be.bewweb.StopWatch.Modele.Listener.RaceListener;
import be.bewweb.StopWatch.Modele.Listener.TeamListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Quentin on 06-02-16.
 */

//TODO: prévoir les tours multiple dans le endTime
public class Team implements Serializable {
    private int dossard;
    private Runner runner1;
    private Runner runner2;
    private long startTime;
    private ArrayList<Long> endTime;
    private boolean registrationValidated;
    private transient ArrayList<TeamListener> teamListeners;

    public Team() {
        this.startTime = 0;
        this.endTime = new ArrayList<>();
        this.registrationValidated = false;
    }

    public int getDossard() {
        return dossard;
    }

    public void setDossard(int dossard) {
        if(this.dossard != dossard){
            this.dossard = dossard;
            fireDossardChanged();
        }
    }

    public Runner getRunner1() {
        return runner1;
    }

    public void setRunner1(Runner runner1) {
        if(runner1 != this.runner1){
            this.runner1 = runner1;
            fireRunner1Changed();
        }
    }

    public Runner getRunner2() {
        return runner2;
    }

    public void setRunner2(Runner runner2) {
        if(runner2 != this.runner2){
            this.runner2 = runner2;
            fireRunner2Changed();
        }
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        if(startTime != this.startTime){
            this.startTime = startTime;
            fireStartTimeChanged();
        }
    }

    public ArrayList<Long> getEndTime() {
        return endTime;
    }

    public void addEndTime(long endTime) {
        if(!this.endTime.contains(endTime)){
            this.endTime.add(endTime);
            fireEndTimeChanged();
        }
    }

    public boolean isRegistrationValidated() {
        return registrationValidated;
    }

    public void setRegistrationValidated(boolean registrationValidated) {
        if(registrationValidated != this.registrationValidated){
            this.registrationValidated = registrationValidated;
            fireRegistrationValidatedChanged();
        }
    }

    //Listener
    public void addListener(TeamListener teamListener) {
        if(this.teamListeners == null){
            this.teamListeners = new ArrayList<>();
        }
        this.teamListeners.add(teamListener);
    }
    public boolean removeListener(TeamListener teamListener) {
        return this.teamListeners.remove(teamListener);
    }
    private void fireDossardChanged(){
        if(this.teamListeners != null){
            ArrayList<TeamListener> teamListeners = (ArrayList) this.teamListeners.clone();
            for (TeamListener listener : teamListeners) {
                listener.dossardChanged(this.dossard);
            }
        }
    }
    private void fireStartTimeChanged(){
        if(this.teamListeners != null){
            ArrayList<TeamListener> teamListeners = (ArrayList) this.teamListeners.clone();
            for (TeamListener listener : teamListeners) {
                listener.startTimeChanged(this.startTime);
            }
        }
    }
    private void fireEndTimeChanged(){
        if(this.teamListeners != null){
            ArrayList<TeamListener> teamListeners = (ArrayList) this.teamListeners.clone();
            for (TeamListener listener : teamListeners) {
                listener.endTimeChanged(this.endTime);
            }
        }
    }
    private void fireRunner1Changed(){
        if(this.teamListeners != null){
            ArrayList<TeamListener> teamListeners = (ArrayList) this.teamListeners.clone();
            for (TeamListener listener : teamListeners) {
                listener.runner1Changed(this.runner1);
            }
        }
    }
    private void fireRunner2Changed(){
        if(this.teamListeners != null){
            ArrayList<TeamListener> teamListeners = (ArrayList) this.teamListeners.clone();
            for (TeamListener listener : teamListeners) {
                listener.runner2Changed(this.runner2);
            }
        }
    }
    private void fireRegistrationValidatedChanged(){
        if(this.teamListeners != null){
            ArrayList<TeamListener> teamListeners = (ArrayList) this.teamListeners.clone();
            for (TeamListener listener : teamListeners) {
                listener.registrationValidatedChanged(this.registrationValidated);
            }
        }
    }

}
