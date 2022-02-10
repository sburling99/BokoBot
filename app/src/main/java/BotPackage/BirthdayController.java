package BotPackage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class BirthdayController {
    private Connection c = null;
    public Logger logger = LoggerFactory.getLogger(App.class);

    public BirthdayController() {}

    private  void openDataBase() {
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:birthday.db");
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

    private  void closeDataBase() throws SQLException {
        c.close();
    }

    public void createTable() {
        Statement s;
        try {
            openDataBase();
            s = c.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS  BIRTHDAYS " +
                    "(ID INTEGER PRIMARY KEY     AUTOINCREMENT," +
                    " NAME           TEXT    NOT NULL, " +
                    " BIRTHDAY       TEXT    NOT NULL) ";
            s.executeUpdate(sql);
            s.close();
            c.close();
            logger.info("Table was created successfully");
        } catch ( Exception e ) {
            logger.error(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        try {
            closeDataBase();
        } catch (SQLException se) {
            logger.error("Unable to close connection to database." + se);
        }
    }

    public void InsertTransaction(String sql){
        Statement s;
        openDataBase();
        try {
            c.setAutoCommit(false);
            s = c.createStatement();
            s.executeUpdate(sql);
            s.close();
            c.commit();
            logger.info("Record was created successfully");
            closeDataBase();
        } catch (SQLException se) {
            logger.error("Unable to set property for database" + se);
        }
    }

    public String selectStatement(String query) {
        Statement s;
        StringBuilder sb = new StringBuilder();

        openDataBase();
        try {
            c.setAutoCommit(false);
            s = c.createStatement();
            ResultSet rs = s.executeQuery(query);
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String birthday = rs.getString("birthday");
                sb.append(name).append(" ").append(birthday).append("\n\n");
            }
            rs.close();
            s.close();
            closeDataBase();
            logger.info("Selection was successful");
        } catch (SQLException se) {
            logger.error("Unable to read from database" + se);
        }
        return sb.toString();
    }

    public String selectUser(String query) {
        Statement s;
        StringBuilder sb = new StringBuilder();

        openDataBase();
        try {
            c.setAutoCommit(false);
            s = c.createStatement();
            ResultSet rs = s.executeQuery(query);
            while (rs.next()) {
                String name = rs.getString("name");
                sb.append(name).append(" ");
            }
            rs.close();
            s.close();
            closeDataBase();
            logger.info("Selection was successful");
        } catch (SQLException se) {
            logger.error("Unable to read from database" + se);
        }
        return sb.toString();
    }

    public void DeleteTransaction(String name){
        String sql = "DELETE FROM BIRTHDAYS WHERE name = ? ";
        openDataBase();
        try (PreparedStatement pstmt = c.prepareStatement(sql)) {
            // set the corresponding param
            pstmt.setString(1, name);
            // execute the delete statement
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}