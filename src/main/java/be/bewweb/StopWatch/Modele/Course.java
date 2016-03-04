package be.bewweb.StopWatch.Modele;

import be.bewweb.StopWatch.Modele.Listener.CourseListener;
import be.bewweb.StopWatch.Modele.Listener.RaceListener;
import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Quentin on 06-02-16.
 */
public class Course implements Serializable {
    private String name;
    private float km;
    private ArrayList<Team> teams;
    private transient ArrayList<CourseListener> courseListeners;
    private Integer numberOfTurns = 1;
    private boolean started;
    private Category category;

    public Course(String name, float km, Integer numberOfTurn) {
        this.name = name;
        this.km = km;
        this.numberOfTurns = numberOfTurn;
        this.teams = new ArrayList<>();
        this.category = new Category();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (this.name != name) {
            this.name = name;
            fireNameChanged();
        }
    }

    public float getKm() {
        return km;
    }

    public void setKm(float km) {
        if (this.km != km) {
            this.km = km;
            fireKmChanged();
        }
    }

    public Category getCategory() {
        if(this.category == null){
            this.category = new Category();
        }
        return this.category;
    }

    public void  addTeam(Team team){
        if(!this.teams.contains(team)) {
            this.teams.add(team);
            fireTeamAdded(team);
        }
    }
    public boolean removeTeam(Team team){
        if(this.teams.contains(team)) {
            this.teams.remove(team);
            fireTeamRemoved(team);
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return this.name + " - " + this.km + "kms";
    }

    protected void fireTeamAdded(Team team) {
        if(this.courseListeners != null){
            ArrayList<CourseListener> courseListeners = (ArrayList) this.courseListeners.clone();
            for (CourseListener listener : courseListeners) {
                listener.teamAdded(team);
            }
        }
    }
    protected void fireTeamRemoved(Team team) {
        if(this.courseListeners != null){
            ArrayList<CourseListener> courseListeners = (ArrayList) this.courseListeners.clone();
            for (CourseListener listener : courseListeners) {
                listener.teamRemoved(team);
            }
        }
    }
    protected void fireKmChanged() {
        if(this.courseListeners != null){
            ArrayList<CourseListener> courseListeners = (ArrayList) this.courseListeners.clone();
            for (CourseListener listener : courseListeners) {
                listener.kmChanged(this.km);
            }
        }
    }
    protected void fireNameChanged() {
        if(this.courseListeners != null){
            ArrayList<CourseListener> courseListeners = (ArrayList) this.courseListeners.clone();
            for (CourseListener listener : courseListeners) {
                listener.nameChanged(this.name);
            }
        }
    }

    public void addListener(CourseListener courseListener) {
        if(this.courseListeners == null){
            this.courseListeners = new ArrayList<>();
        }
        this.courseListeners.add(courseListener);
    }

    public boolean removeListener(CourseListener courseListener) {
        return this.courseListeners.remove(courseListener);
    }

    public ArrayList<Team> getTeams() {
        return this.teams;
    }

    public void setTeams(ArrayList<Team> teams) {
        this.teams = teams;
    }

    public Integer getNumberOfTurns() {
        return numberOfTurns;
    }

    public void setNumberOfTurns(Integer numberOfTurns) {
        this.numberOfTurns = numberOfTurns;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }
}
