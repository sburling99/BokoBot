package BotPackage;

import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class JdbcController {
    private Connection c = null;
    public Logger logger = LoggerFactory.getLogger(App.class);

    public JdbcController() {}

    private  void openDataBase() {
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:events.db");
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
            String sql = "CREATE TABLE IF NOT EXISTS  COMPANY " +
                    "(ID INTEGER PRIMARY KEY     AUTOINCREMENT," +
                    " NAME           TEXT    NOT NULL, " +
                    " AGE            INT     NOT NULL, " +
                    " ADDRESS        CHAR(50), " +
                    " SALARY         REAL)";
            s.executeUpdate(sql);

            s.close();
            c.close();
            logger.info("Table created successfully");
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
            logger.info("Record created successfully");
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
                int age = rs.getInt("age");
                String address = rs.getString("address");
                float salary = rs.getFloat("salary");
                sb.append("ID = ").append(id).append("\n")
                        .append("NAME = ").append(name).append("\n")
                        .append("AGE = ").append(age).append("\n")
                        .append("ADDRESS = ").append(address).append("\n")
                        .append("SALARY = ").append(salary).append("\n\n");

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

    public void createLog() {
        Statement s;
        try {
            openDataBase();
            s = c.createStatement();
            String dataTable = "CREATE TABLE IF NOT EXISTS  EVENTS " +
                    "(EventID INTEGER PRIMARY KEY     AUTOINCREMENT," +
                    " Name           VARCHAR(50)    NOT NULL, " +
                    " Time           char(50)    NOT NULL, " +
                    " Date           char(50), " +
                    " Location       char(50), " +
                    " Description    char(50))";
            s.executeUpdate(dataTable);

            s.close();
            c.close();
            logger.info("Data Table created successfully");
        } catch ( Exception e ) {
            e.printStackTrace();
            System.out.println("Error Creating Table!");
            logger.error(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        try {
            closeDataBase();

        } catch (SQLException se) {
            logger.error("Unable to close connection to database." + se);
        }
    }

    public boolean insertEvent(String sql){

        Statement s;
        openDataBase();

        try {
            c.setAutoCommit(false);
            s = c.createStatement();
            s.executeUpdate(sql);
            s.close();
            c.commit();
            logger.info("Event Logged");
            closeDataBase();
            return true;
        } catch (SQLException se) {
            logger.error("Unable to set property for database" + se);
            se.printStackTrace();
        }
        return false;
    }

    public ArrayList<EmbedBuilder> selectEvent(String query) {

        Statement s;
        openDataBase();
        ArrayList<EmbedBuilder> listOfEvents = new ArrayList<>();
        try {
            c.setAutoCommit(false);
            s = c.createStatement();
            ResultSet rs = s.executeQuery(query);
            while ( rs.next() ) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle("Texas State Event: " + rs.getString("Name"));
                embed.addField("Event Name: ", rs.getString("Name"), false);
                embed.addField("Event Date: ", rs.getString("Date"), false);
                embed.addField("Event Time: ", rs.getString("Time"), false);
                embed.addField("Event Location: ", rs.getString("Location"), false);
                embed.addField("Event Description: ", rs.getString("Description"), false);

//                String id = rs.getString("EventId");
//                String name = rs.getString("Name");
//                String date = rs.getString("Date");
//                String time = rs.getString("Time");
//                String location = rs.getString("Location");
//                String description = rs.getString("Description");
//                sb.append("ID = ").append(id).append("\n")
//                        .append("Name = ").append(name).append("\n")
//                        .append("Time = ").append(time).append("\n")
//                        .append("Date = ").append(date).append("\n")
//                        .append("Location = ").append(location).append("\n")
//                        .append("Description = ").append(description).append("\n\n");
                embed.setColor(new Color(0x42060D));
                listOfEvents.add(embed);
            }

            rs.close();
            s.close();
            closeDataBase();
            logger.info("Selection was successful");
        } catch (SQLException se) {
            logger.error("Unable to read from database" + se);
        }
        return listOfEvents;
    }
}