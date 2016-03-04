package be.bewweb.StopWatch.Modele.Listener;

import be.bewweb.StopWatch.Modele.Runner;
import be.bewweb.StopWatch.Modele.Team;

import java.util.ArrayList;

/**
 * Created by Quentin on 07-02-16.
 */
public interface TeamListener {
    public void dossardChanged(int dossard);
    public void startTimeChanged(long start);
    public void endTimeChanged(ArrayList<Long> end);
    public void runner1Changed(Runner runner);
    public void runner2Changed(Runner runner);
    public void registrationValidatedChanged(Boolean registrationValidated);
}
