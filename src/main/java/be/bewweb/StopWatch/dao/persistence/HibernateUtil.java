package be.bewweb.StopWatch.dao.persistence;

import be.bewweb.StopWatch.view.ManageTeamView;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.sql.*;
import java.util.Optional;
import java.util.TimeZone;

/**
 * This class will instantiate the sessionFactory of hibernate
 *
 * @author Quentin Lombat
 */


public class HibernateUtil {
    private static SessionFactory sessionFactory;
    private static Configuration configuration;

    public static DateTime getServerDateTime() {
        String connectionUrl = configuration.getProperty("hibernate.connection.url");
        String password = configuration.getProperty("connection.password");
        String user = configuration.getProperty("connection.username");
        String driver = configuration.getProperty("connection.driver_class");
        Connection conn = null;
        Statement stmt = null;
        try {
            //STEP 2: Register JDBC driver
            Class.forName(driver);

            //STEP 3: Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(connectionUrl, user, password);

            //STEP 4: Execute a query
            System.out.println("Creating statement...");
            stmt = conn.createStatement();
            String sql;
            sql = "SELECT SYSDATE(3)+0, @@system_time_zone";
            ResultSet rs = stmt.executeQuery(sql);
            rs.next();

            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMMddHHmmss.SSSz");
            DateTime dateTime = formatter.parseDateTime(rs.getString(1) + rs.getString(2));

            rs.close();

            return dateTime.withZone(DateTimeZone.getDefault());


        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            //finally block used to close resources
            try {
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return new DateTime();
    }

    public synchronized static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                // creation of the SessionFactory from hibernate.cfg.xml
                configuration = new Configuration().configure();
                sessionFactory = configuration.buildSessionFactory();
            } catch (Throwable ex) {
                System.err.println("Initial SessionFactory creation failed." + ex);
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Problème avec la base de données");
                alert.setHeaderText("Une erreur est survenue lors de l'accès à la base de données");
                alert.setContentText("Impossible de se connecter à la base de données pour le moment.");

                Optional<ButtonType> result = alert.showAndWait();
            }
        }

        return sessionFactory;
    }
}
