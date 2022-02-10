package BotPackage;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.regex.*;

public class Birthday extends ListenerAdapter {
    BirthdayController birthdayDB = new BirthdayController();
    DateCustom date = new DateCustom();
    String today = date.getTodaysDate();
    BirthdayQueries birthdayquery = new BirthdayQueries();

    public void addBirthday(GuildMessageReceivedEvent event, String[] args) {
        String name = args[1];
        String birthday = args[2];
        String query = birthdayquery.insertionQuery(name, birthday);
        birthdayDB.createTable();
        birthdayDB.InsertTransaction(query);
    }

    public void birthdaysList(GuildMessageReceivedEvent event) {
        String results = birthdayDB.selectStatement("SELECT * FROM BIRTHDAYS");
        event.getChannel().sendMessage("Birthdays: \n").queue();
        event.getChannel().sendMessage(results).queue();
    }

    public void birthdaysToday(GuildMessageReceivedEvent event) {
        String query = birthdayquery.getByDateQuery(today);
        String results = birthdayDB.selectStatement(query);
        event.getChannel().sendMessage("Birthdays: \n").queue();
        event.getChannel().sendMessage(results).queue();
    }

    public void deleteBirthday(GuildMessageReceivedEvent event, String[] args) {
        birthdayDB.DeleteTransaction(args[1]);
    }
//TODO : Allow for message of more than one word
    public void sendBirthdayMessage(GuildMessageReceivedEvent event, String[] args) {
        String message = "";

        for(int i = 1; i < args.length; i++) {
            message += args[i] + " ";
        }
        final String finalMessage = message;

        String results = birthdayDB.selectUser(birthdayquery.getByDateQuery(today));

        String resultsArray[] = results.split(" ");
        List<String> resultsList = new ArrayList<String>();
        resultsList = Arrays.asList(resultsArray);

        for(String user: resultsList) {
            user = user.replace("<","");
            user = user.replace("!","");
            user = user.replace("@","");
            user = user.replace(">","");
            System.out.println(user);
            long idNumber = Long.parseLong(user);
            event.getJDA().openPrivateChannelById(idNumber).flatMap(schannel -> schannel.sendMessage(finalMessage)).queue();
        }

    }
}