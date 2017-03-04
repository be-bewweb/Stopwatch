package be.bewweb.StopWatch.dao.beans;


import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Quentin Lombat
 */

@Entity
@Table(name = "team")
public class Team implements Serializable {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "bib")
    private Integer bib;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_runner1")
    private Runner runner1;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_runner2")
    private Runner runner2;

    @Column(name = "start")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime startTime;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name="ends", joinColumns=@JoinColumn(name="id_team"))
    @Column(name="end", length=3)
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Fetch(FetchMode.SELECT)
    @Temporal(javax.persistence.TemporalType.DATE)
    private List<DateTime> endTime = new ArrayList<>();

    @Column(name = "registration_validated")
    private boolean registrationValidated;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_course")
    @Fetch( FetchMode.SELECT)
    private Course course;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getBib() {
        return bib;
    }

    public void setBib(Integer bib) {
        this.bib = bib;
    }

    public be.bewweb.StopWatch.dao.beans.Runner getRunner1() {
        return runner1;
    }

    public void setRunner1(be.bewweb.StopWatch.dao.beans.Runner runner1) {
        this.runner1 = runner1;
    }

    public be.bewweb.StopWatch.dao.beans.Runner getRunner2() {
        return runner2;
    }

    public void setRunner2(be.bewweb.StopWatch.dao.beans.Runner runner2) {
        this.runner2 = runner2;
    }

    public DateTime getStartTime() {
        if(startTime == null) return new DateTime(0);
        return startTime;
    }

    public void setStartTime(DateTime startTime) {
        this.startTime = startTime;
    }

    public List<DateTime> getEndTime() {
        return endTime;
    }

    public void setEndTime(List<DateTime> endTime) {
        this.endTime = endTime;
    }

    public boolean isRegistrationValidated() {
        return registrationValidated;
    }

    public void setRegistrationValidated(boolean registrationValidated) {
        this.registrationValidated = registrationValidated;
    }

    public Long getTime(){
        if(startTime == null) return null;
        return this.endTime.get(this.endTime.size()-1).getMillis() - this.startTime.getMillis();
    }

    public Long getTime(Integer turn){
        if(startTime == null || endTime == null || endTime.size() < turn) return null;
        return this.endTime.get(turn-1).getMillis() - this.startTime.getMillis();
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public boolean getArrived(){
        return this.course.getNumberOfTurns() <= this.endTime.size();
    }

    public String getType(){
        if(this.runner1 == null || this.runner2 == null){
            return "n/a";
        }
        if(this.runner1.isMan() && this.runner2.isMan()){
            return "Masculin";
        }
        if(!this.runner1.isMan() && !this.runner2.isMan()){
            return "Féminin";
        }
        return "Mixte";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Team team = (Team) o;

        return id != null ? id.equals(team.id) : team.id == null;
    }

    @Override
    public int hashCode() {
        Integer result = id != null ? id.hashCode() : 0;
        result = 31 * result + bib;
        result = 31 * result + (runner1 != null ? runner1.hashCode() : 0);
        result = 31 * result + (runner2 != null ? runner2.hashCode() : 0);
        result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
        result = 31 * result + (endTime != null ? endTime.hashCode() : 0);
        result = 31 * result + (registrationValidated ? 1 : 0);
        result = 31 * result + (course != null ? course.hashCode() : 0);
        return result;
    }
}
