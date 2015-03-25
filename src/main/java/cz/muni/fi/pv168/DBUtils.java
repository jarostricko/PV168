package cz.muni.fi.pv168;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Some DB tools.
 *
 * @author Petr Adamek
 */
public class DBUtils {


    /**
     * Reads SQL statements from file. SQL commands in file must be separated by
     * a semicolon.
     *
     * @param is input stream of the file
     * @return array of command  strings
     */
    private static String[] readSqlStatements(InputStream is) {
        try {
            char buffer[] = new char[256];
            StringBuilder result = new StringBuilder();
            InputStreamReader reader = new InputStreamReader(is, "UTF-8");
            while (true) {
                int count = reader.read(buffer);
                if (count < 0) {
                    break;
                }
                result.append(buffer, 0, count);
            }
            return result.toString().split(";");
        } catch (IOException ex) {
            throw new RuntimeException("Cannot read ", ex);
        }
    }


    /**
     * Executes SQL script.
     *
     * @param ds datasource
     * @param is sql script to be executed
     * @throws java.sql.SQLException when operation fails
     */
    public static void executeSqlScript(DataSource ds, InputStream is) throws SQLException {
        try (Connection conn = ds.getConnection()) {
            for (String sqlStatement : readSqlStatements(is)) {
                if (!sqlStatement.trim().isEmpty()) {
                    try (PreparedStatement preparedStatement = conn.prepareStatement(sqlStatement)) {
                        preparedStatement.executeUpdate();
                    }
                }
            }
        }
    }
}
