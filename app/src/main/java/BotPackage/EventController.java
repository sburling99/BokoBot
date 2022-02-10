package BotPackage;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.sql.*;

public class EventController extends ListenerAdapter {

    /*
    * Establishes database connection that'll be used by
    * all the methods
    * */
    private Connection connect() {

        String url = "jdbc:sqlite:events.db";
        Connection conn = null;

        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return conn;
    }

    /*
    *Receives message from user and determines which
    * method is being called
    */
    public void onGuildMessageReceived(GuildMessageReceivedEvent event){
        String[] args = event.getMessage().getContentRaw().split("%");

        if (args[0].equalsIgnoreCase(App.prefix +
                "Event")){
            displayEventCreated(event, args);
        }

        if (args[0].equalsIgnoreCase(App.prefix +
                "TXSTEvent")){
            texasStateSite(event, args);
        }

        if (args[0].equalsIgnoreCase(App.prefix + "Remove")) {
            try {
                removeEvent(event, args);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        }

        if (args[0].equalsIgnoreCase(App.prefix + "List")) {
            try {
                listAllEvents(event);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        }

        if (args[0].equalsIgnoreCase(App.prefix + "Display")) {
            try {
                displayEvent(event, args);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

    }

    /*
    * Displays the event created.
    * Calls a method to add event to the database.
    * */
    private void displayEventCreated(GuildMessageReceivedEvent event, String[] args) {

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Texas State Event");
        String name = args[1];
        String time = args[2];
        String date = args[3];
        String address = args[4];
        String description = args[5];
        String creator = event.getMessage().getAuthor().getName();

        embed.addField("Event Created by: ", creator,true);
        embed.addField("Event Name: ",name, false);
        embed.addField("Time: ",time, true);
        embed.addField("Date: ",date, true);
        embed.addField("Location: ",address,false);
        embed.addField("Description: ",description,false);
        embed.setColor(new Color(0x42060D));
        event.getChannel().sendMessageEmbeds(embed.build()).queue();

        try {
            addEvent(event, args);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        embed.clear();
    }

    /*
    * Publishes the link to the Texas State Events site.
    * */
    private void texasStateSite(GuildMessageReceivedEvent event, String[] args) {

        EmbedBuilder embedlink = new EmbedBuilder();
        embedlink.setTitle("Texas State Events for Today");
        String txsturl = "https://events.txstate.edu";

        embedlink.setTitle("Texas State Events for Today",txsturl);
        embedlink.setColor(new Color(0x42060d));
        event.getChannel().sendMessageEmbeds(embedlink.build()).queue();
        embedlink.clear();

    }

    /*
    * Adds the event to the database.
    * */
    private void addEvent(GuildMessageReceivedEvent event, String[] args) throws SQLException {

        final String INSERT_SQL = "INSERT INTO EVENTS (EventID, Name, Time, Date, Location, Description) VALUES(?, ?, ?, ?, ?, ?)";

        Connection conn = this.connect();
        PreparedStatement ps = conn.prepareStatement(INSERT_SQL);
        ps.setString(1, null);
        ps.setString(2, args[1]);
        ps.setString(3, args[2]);
        ps.setString(4, args[3]);
        ps.setString(5, args[4]);
        ps.setString(6, args[5]);
        ps.executeUpdate();

        event.getChannel().sendMessage("Event Added").queue();

    }

    /*
    * Removes event from database based on the name of the event.
    * */
    private void removeEvent(GuildMessageReceivedEvent event, String[] args) throws SQLException {

        String name = args[1];
        final String removeSQL = "DELETE FROM EVENTS WHERE Name = ?";

        Connection conn = this.connect();
        PreparedStatement ps = conn.prepareStatement(removeSQL);

        ps.setString(1, name);
        ps.executeUpdate();

        event.getChannel().sendMessage("Event Removed").queue();

    }

    /*
    * Publishes a list of events.
    * */
    private void listAllEvents(GuildMessageReceivedEvent event) throws SQLException {

        String listAll = "SELECT Name FROM EVENTS";
        Connection conn = this.connect();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(listAll);

        while (rs.next()) {
            String message = rs.getString("Name");
            event.getChannel().sendMessage(message).queue();
        }
    }

    /*
    * Displays a specific event based on the name of it.
    * */
    private void displayEvent(GuildMessageReceivedEvent event, String[] args) throws SQLException {

        String selectedEvent = "SELECT Name, Time, Date, Location, Description FROM EVENTS WHERE Name = ?";
        String name = args[1];
        Connection conn = this.connect();
        PreparedStatement state = conn.prepareStatement(selectedEvent);

        state.setString(1, name);
        ResultSet rs = state.executeQuery();

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Texas State Event");

        while(rs.next()) {

            String Name = rs.getString("Name");
            String Time = rs.getString("Time");
            String Date = rs.getString("Date");
            String Location = rs.getString("Location");
            String Description = rs.getString("Description");

            embed.addField("Event Name: ", Name, false);
            embed.addField("Time: ", Time, true);
            embed.addField("Date: ", Date, true);
            embed.addField("Location: ", Location,false);
            embed.addField("Description: ", Description,false);
            embed.setColor(new Color(0x42060D));

            event.getChannel().sendMessageEmbeds(embed.build()).queue();

        }
    }
}
