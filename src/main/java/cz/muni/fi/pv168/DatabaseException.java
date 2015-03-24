package cz.muni.fi.pv168;

/**
 * Created by Jaro on 18.3.2015.
 */
public class DatabaseException extends Exception{

    public DatabaseException(Throwable cause) {
        super(cause);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException() {
    }
}
