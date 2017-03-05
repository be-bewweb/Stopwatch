package be.bewweb.StopWatch.dao.persistence.Repository;

import be.bewweb.StopWatch.dao.beans.Course;
import be.bewweb.StopWatch.dao.beans.Race;
import be.bewweb.StopWatch.dao.beans.Team;
import be.bewweb.StopWatch.dao.persistence.HibernateUtil;
import be.bewweb.StopWatch.exception.DatabaseException;
import org.hibernate.CacheMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

/**
 * @author Quentin Lombat
 */
public class Repository<T> {

    protected SessionFactory sessionFactory;
    protected Class<T> typeParameterClass;

    public Repository(Class<T> typeParameterClass) {
        this.sessionFactory = HibernateUtil.getSessionFactory();
        this.typeParameterClass = typeParameterClass;
    }

    public void merge(T obj) throws DatabaseException {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.merge(obj);
            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction != null) transaction.rollback();
            throw new DatabaseException(e.getMessage());
        } finally {
            session.close();
        }
    }
    public void save(T obj) throws DatabaseException {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.save(obj);
            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction != null) transaction.rollback();
            throw new DatabaseException(e.getMessage());
        } finally {
            session.close();
        }
    }

    public T find(Long id) throws DatabaseException {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            T res = session.get(typeParameterClass, id);
            transaction.commit();
            return res;
        } catch (RuntimeException e) {
            if (transaction != null) transaction.rollback();
            throw new DatabaseException(e.getMessage());
        } finally {
            session.close();
        }
    }

    public List<T> findAll() throws DatabaseException {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            List<T> races = session.createQuery("from " + this.typeParameterClass.getName()).list();
            transaction.commit();
            return races;
        } catch (RuntimeException e) {
            if (transaction != null) transaction.rollback();
            throw new DatabaseException(e.getMessage());
        } finally {
            session.close();
        }
    }

    public void remove(T obj) throws DatabaseException {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.remove(obj);
            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction != null) transaction.rollback();
            throw new DatabaseException(e.getMessage());
        } finally {
            session.close();
        }
    }


}
