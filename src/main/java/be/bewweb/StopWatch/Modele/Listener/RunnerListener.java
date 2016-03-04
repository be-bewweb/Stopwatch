package be.bewweb.StopWatch.Modele.Listener;

import java.util.Date;

/**
 * Created by Quentin on 07-02-16.
 */
public interface RunnerListener {
    public void nameChanged(String name);

    public void firstnameChanged(String firstname);

    public void sexChanged(boolean isMan);

    public void birthDateChanged(Date birthDate);
}
