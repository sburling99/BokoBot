package BotPackage;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class BotController extends ListenerAdapter {
    Birthday bday = new Birthday();

    private final List<String> listOfCommands;
    private final BusRouteController brc = new BusRouteController(new TreeMap<>(), new TreeMap<>(), new TreeMap<>());


    public BotController() {
        listOfCommands = CommandListUtil.commandsAsList();
        readAllBusCSVInfo();
    }
    public void onGuildMessageReceived(GuildMessageReceivedEvent event){
        String[] args = event.getMessage().getContentRaw().split(" ");
        if (args[0].equalsIgnoreCase(App.prefix + "commands")) {
            listAllCommands(event);
        }
        if (args[0].equalsIgnoreCase(App.prefix + "waddup")){
            waddup(event);
        }
        if (args[0].equalsIgnoreCase(App.prefix + "hmmm")){
            hmmm(event);
        }
        if (args[0].equalsIgnoreCase(App.prefix + "hippity")){
            hippity(event);
        }
        if (args[0].equalsIgnoreCase(App.prefix + "bday")){
            bday(event);
        }
        if (args[0].equalsIgnoreCase(App.prefix + "sqlTest")) {
            sqlTest(event, args);
        }
        if (args[0].equalsIgnoreCase(App.prefix + "neighbor")) {
            neighbor(event);
        }
        if (args[0].equalsIgnoreCase(App.prefix + "addBirthday")) {
            bday.addBirthday(event, args);
        }
        if (args[0].equalsIgnoreCase(App.prefix + "birthdaysList")) {
            bday.birthdaysList(event);
        }
        if (args[0].equalsIgnoreCase(App.prefix + "birthdaysToday")) {
            bday.birthdaysToday(event);
        }
        if (args[0].equalsIgnoreCase(App.prefix + "sendBirthdayMessage")) {
            bday.sendBirthdayMessage(event, args);
        }
        if (args[0].equalsIgnoreCase(App.prefix + "deleteBirthday")) {
            bday.deleteBirthday(event, args);
        }
        if (args[0].equalsIgnoreCase(App.prefix + "busStopGPS")) {
            getBusStopGPS(event, args);
        }
        if (args[0].equalsIgnoreCase(App.prefix + "allBusStops")) {
            displayAllBusStops(event);
        }
        if (args[0].equalsIgnoreCase(App.prefix + "bus")) {
            try {
                busController(event, Arrays.copyOfRange(args, 1, args.length));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (args[0].equalsIgnoreCase(App.prefix + "coords")) {
            try {
                getCoordinates(event, args);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (args[0].equalsIgnoreCase(App.prefix + "closestStop")) {
            try {
                getClosestBusStop(event, args);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void listAllCommands(GuildMessageReceivedEvent event) {
        StringBuilder sb = new StringBuilder();
        for (String item: listOfCommands) {
            sb.append("-").append(item).append("\n");
        }
        event.getChannel().sendMessage(sb.toString()).queue();
    }
    public void waddup(GuildMessageReceivedEvent event){
        event.getChannel().sendMessage("wadddduuuup").queue();
    }

    public void hmmm(GuildMessageReceivedEvent event) {
        event.getChannel().sendMessage("*cloaked man removes hood* Hello there.").queue();
    }

    public void hippity(GuildMessageReceivedEvent event){
        event.getChannel().sendMessage("hop").queue();
    }

    public void bday(GuildMessageReceivedEvent event){
        event.getChannel().sendMessage("Happy Birthday!").queue();
    }

    public void sqlTest(GuildMessageReceivedEvent event, String[] args){
        JdbcController jdbc = new JdbcController();
        ArrayList<EmbedBuilder> results = jdbc.selectEvent("SELECT * FROM EVENTS");
        for (EmbedBuilder eb: results) {
            event.getChannel().sendMessageEmbeds(eb.build()).queue();
        }
    }

    public void neighbor(GuildMessageReceivedEvent event) {
        event.getChannel().sendMessage(
                "Often when you think you're at the end of something, you're at the beginning of something else."
        ).queue();
    }

    public void getBusStopGPS(GuildMessageReceivedEvent event, String[] busStopName) {
        StringBuilder busStopFullName = new StringBuilder();
        busStopName = Arrays.copyOfRange(busStopName, 1, busStopName.length);
        for (String word: busStopName) {
            word = word.toUpperCase(Locale.ROOT);
            busStopFullName.append(word).append(" ");
        }
        busStopFullName = new StringBuilder(busStopFullName.toString().trim());
        event.getChannel().sendMessage(brc.displayBusStopGPS(busStopFullName.toString())).queue();
    }

    public void displayAllBusStops(GuildMessageReceivedEvent event) {
        event.getChannel().sendMessage(brc.displayAllStops()).queue();
    }

    public void getCoordinates(GuildMessageReceivedEvent event, String[] args) throws IOException {
        GoogleHandler gh = new GoogleHandler();
        event.getChannel().sendMessage(gh.GeoCoder(Arrays.copyOfRange(args, 1, args.length))).queue();
    }

    public void getClosestBusStop(GuildMessageReceivedEvent event, String[] args) throws IOException {
        Double[] gpsCoordinates = GoogleHandler.getGPSCoordinates(Arrays.copyOfRange(args, 1, args.length));
        BusStop closestStop = brc.getClosestBusStop(gpsCoordinates[0], gpsCoordinates[1]);
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(0x42060D));
        //Check for Sunday Request
        if (brc.getDayOfWeek().equals("Sunday")) {
            embed.setTitle("Sunday query");
            embed.addField("Sorry!", "TXST Buses do not run on Sundays.", false);
        //Check to see if current time is valid
        } else if (!brc.validBusRouteTime()) {
            embed.setTitle("Invalid Bus Time");
            embed.addField("Sorry!", "TXST Buses currently not running.", false);
        } else {
            try {
                BusRoute realTimeBusRoute = brc.getRealTimeBusRoute(closestStop);
                String gmapPinUrl = "https://www.google.com/maps/search/?api=1&query=" + closestStop.getLatitude() +
                        "%2C" + closestStop.getLongitude();
                String doubleMapUrl = "https://txstate.doublemap.com/map/";

                EmbedBuilder privateMessage = new EmbedBuilder();
                privateMessage.setColor(new Color(0x42060D));

                privateMessage.setTitle("Current Bus Information");
                privateMessage.addField("Bus Route", realTimeBusRoute.getRouteName(), false);
                privateMessage.addField("Bus Stop", closestStop.getName(), false);
                privateMessage.addField("Google Pin", gmapPinUrl, false);
                privateMessage.addField("Live Bus Map", doubleMapUrl, false);
                privateMessage.addField("Note","When clicking on DoubleMap, flip the \"" +
                                realTimeBusRoute.getRouteName() + "\" option", false);
                embed.addField("Bus Route Query","<@" + event.getAuthor().getIdLong() + "> " + " Sent you bus information in a DM!", true);
                //Send User Private Message
                event.getAuthor()
                        .openPrivateChannel()
                        .flatMap(channel ->
                                channel.sendMessageEmbeds(privateMessage.build())).queue();

            } catch (NullPointerException n) {
                embed.setTitle("Unknown Error");
                embed.addField("Sorry!", "Something went wrong. Please click the " +
                        "link below to see current bus routes (if any).", false);
                embed.addField("Live Bus Map", "https://txstate.doublemap.com/map/", false);
            }
        }
        //send message to main channel
        event.getChannel().sendMessageEmbeds(embed.build()).queue();
    }

    public void busController(GuildMessageReceivedEvent event, String[] args) throws IOException {
        if (args[0].equalsIgnoreCase("to")){
            event.getChannel().sendMessage("-bus to *address* is a work in progress!").queue();
        }else if (args[0].equalsIgnoreCase("from")){
            System.out.println(Arrays.toString(Arrays.copyOfRange(args, 1, args.length)));
            getClosestBusStop(event, Arrays.copyOfRange(args, 2, args.length));
        }else{
            event.getChannel().sendMessage("Not a valid input!").queue();
        }
    }

    public void readAllBusCSVInfo() {
        try {
            brc.readBusStopCSVInfo("C:\\Users\\Stephen\\bokobot_repository\\BokoBot\\app\\src\\main\\data\\BusStopInfo.csv");
            brc.readBusRouteCSVInfo("C:\\Users\\Stephen\\bokobot_repository\\BokoBot\\app\\src\\main\\data\\BusRouteInfo.csv");
            brc.readBusRouteTimeCSVInfo("C:\\Users\\Stephen\\bokobot_repository\\BokoBot\\app\\src\\main\\data\\BusRouteTimes.csv");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
