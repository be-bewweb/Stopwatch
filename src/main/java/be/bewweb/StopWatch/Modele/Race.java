package be.bewweb.StopWatch.Modele;

import be.bewweb.StopWatch.Modele.Listener.RaceListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import java.io.*;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Created by Quentin on 06-02-16.
 */
public class Race implements Serializable {

    private String name;
    private String path;
    private transient ArrayList<RaceListener> raceListeners;
    private static Race instance = null;
    private  ArrayList<Course> courses;


    private Race() {
        this.courses = new ArrayList<>();
    }

    public static Race getInstance() {
        if (instance == null) {
            synchronized (Race.class) {
                if (instance == null) {
                    instance = new Race();
                }
            }
        }
        return instance;
    }

    public void setName(String name) {
        if (this.name != name) {
            this.name = name;
            fireNameChanged();
        }
    }

    public ArrayList<Course> getCourses() {
        return courses;
    }

    public void addCourse(Course c) {
        if(!this.courses.contains(c)) {
            this.courses.add(c);
            fireCoursesAdded(c);
        }
    }

    public boolean removeCourse(Course c) {
        if(this.courses.contains(c)) {
            this.courses.remove(c);
            fireCoursesRemoved(c);
            return true;
        }
        return false;
    }

    public String getName() {
        return name;
    }

    public void reset() {
        instance = null;
    }

    protected void fireNameChanged() {
        if(this.raceListeners != null){
            ArrayList<RaceListener> raceListeners = (ArrayList) this.raceListeners.clone();
            for (RaceListener raceListener : raceListeners) {
                raceListener.nameChanged(this.name);
            }
        }
    }

    protected void fireCoursesAdded(Course course) {
        if(this.raceListeners != null){
            ArrayList<RaceListener> raceListeners = (ArrayList) this.raceListeners.clone();
            for (RaceListener raceListener : raceListeners) {
                raceListener.courseAdded(course);
            }
        }
    }
    protected void fireCoursesRemoved(Course course) {
        if(this.raceListeners != null){
            ArrayList<RaceListener> raceListeners = (ArrayList) this.raceListeners.clone();
            for (RaceListener raceListener : raceListeners) {
                raceListener.courseRemoved(course);
            }
        }
    }
    protected void firePathChanged() {
        if(this.raceListeners != null){
            ArrayList<RaceListener> raceListeners = (ArrayList) this.raceListeners.clone();
            for (RaceListener raceListener : raceListeners) {
                raceListener.pathChanged(this.path);
            }
        }
    }

    public void addListener(RaceListener raceListener) {
        if(this.raceListeners == null){
            this.raceListeners = new ArrayList<>();
        }

        this.raceListeners.add(raceListener);
    }

    public boolean removeListener(RaceListener raceListener) {
        return this.raceListeners.remove(raceListener);
    }

    public void save() {
        try {
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.path + "/" + this.name + ".race"), "UTF-8"));
            writer.write(new GsonBuilder().create().toJson(this));
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NotSerializableException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void load(String completPath){
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(completPath), "UTF8"));
            instance = new GsonBuilder().create().fromJson(br, Race.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
        String[] completPathSplited =  completPath.toString().split(Pattern.quote(File.separator.toString()));
        String path = "";
        for(int i = 0; i < completPathSplited.length - 1; i++){
            path += completPathSplited[i] + File.separator.toString();
        }
        instance.path = path;
        File file = new File(completPath.toString());
        instance.name = file.getName().split(".race")[0];
        instance.save();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        if (this.path != path) {
            this.path = path;
            firePathChanged();
        }
    }
}
