package be.bewweb.StopWatch.Modele.Listener;

import be.bewweb.StopWatch.Modele.Course;

import java.io.Serializable;

/**
 * Created by Quentin on 06-02-16.
 */
public interface RaceListener extends Serializable {
    public void nameChanged(String name);

    public void pathChanged(String path);

    public void courseAdded(Course course);

    public void courseRemoved(Course course);
}
