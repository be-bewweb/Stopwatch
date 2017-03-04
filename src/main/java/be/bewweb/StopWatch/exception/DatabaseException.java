package be.bewweb.StopWatch.exception;

/**
 * @author Quentin Lombat
 */
public class DatabaseException  extends Exception {
    public DatabaseException(String message) {
        super(message);
    }
    public DatabaseException() {
        super();
    }
}