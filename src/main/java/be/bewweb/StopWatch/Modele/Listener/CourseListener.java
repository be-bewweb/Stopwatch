package be.bewweb.StopWatch.Modele.Listener;

import be.bewweb.StopWatch.Modele.Team;

import java.util.ArrayList;

/**
 * Created by Quentin on 07-02-16.
 */
public interface CourseListener {
    public void nameChanged(String name);
    public void kmChanged(float km);
    public void teamAdded(Team team);
    public void teamRemoved(Team team);
}
