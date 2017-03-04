package be.bewweb.StopWatch.dao.persistence.Repository;

import be.bewweb.StopWatch.dao.beans.Race;
import be.bewweb.StopWatch.dao.beans.Team;
import be.bewweb.StopWatch.dao.persistence.HibernateUtil;
import be.bewweb.StopWatch.exception.DatabaseException;
import org.hibernate.CacheMode;
import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

/**
 * @author Quentin Lombat
 */
public class TeamRepository extends Repository<Team> {

    public TeamRepository() {
        super(Team.class);
    }

    public Team findOneByRaceAndBib(Race race, int bib) throws DatabaseException {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            Query query = session.createQuery("from Team where course in (:courses) and bib=:bib");
            Team team = (Team) query.setParameter("courses", race.getCourses()).setParameter("bib", bib).uniqueResult();
            session.getTransaction().commit();
            return team;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new DatabaseException();
        }finally {
            session.close();
        }
    }

    public void addEndTime(Team team) throws DatabaseException {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            Query query = session.createQuery("update Team set endTime = current_date where id = :id");
            query.setParameter("id", team.getId());
            int result = query.executeUpdate();
            session.getTransaction().commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new DatabaseException(e.getMessage());
        }finally {
            session.close();
        }
    }
}
