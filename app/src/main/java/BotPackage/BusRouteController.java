package BotPackage;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class BusRouteController {
    private final Map<String, BusStop> setOfStops;
    private final Map<String, BusRoute> setOfRoutes;
    private final Map<String, LinkedList<BusRouteTimesWrapper>> listOfRoutesForTime;

    public BusRouteController(Map<String, BusStop> setOfStops,
                              Map<String, BusRoute> setOfRoutes,
                              Map<String, LinkedList<BusRouteTimesWrapper>> listOfRoutesForTime) {
        this.setOfStops = setOfStops;
        this.setOfRoutes = setOfRoutes;
        this.listOfRoutesForTime = listOfRoutesForTime;
    }

    public String displayBusStopGPS(String busStopName) {
        if(!containsBusStop(busStopName)) {
            return "Sorry, bus stop not found.";
        }
        try {
            String fullBusStopName = getFullBusStopName(busStopName);
            return setOfStops.get(fullBusStopName).getName() + " is located at: \n" +
                    "Latitude: " + setOfStops.get(fullBusStopName).getLatitude() +
                    "\nLongitude: " + setOfStops.get(fullBusStopName).getLongitude();
        } catch (NullPointerException e) {
            e.printStackTrace();
            return "Sorry, bus stop not found";
        }
    }

    public LinkedList<BusRoute> routesWithBusStop(String busStopName) {
        LinkedList<BusRoute> listOfRoutesWithBusStop = new LinkedList<>();
        for (Map.Entry<String, BusRoute> busRouteEntry : setOfRoutes.entrySet()) {
            if(busRouteEntry.getValue().containsBusStop(busStopName)) {
                listOfRoutesWithBusStop.add(busRouteEntry.getValue());
            }
        }
        return listOfRoutesWithBusStop;
    }

    public boolean containsBusStop(String busStopName) {
        for (Map.Entry<String, BusStop> e : setOfStops.entrySet()) {
            if(e.getKey().contains(busStopName) && !busStopName.equalsIgnoreCase("The")) {
                return true;
            }
        }
        return false;
    }

    private String getFullBusStopName(String busStopName) {
        for (Map.Entry<String, BusStop> e : setOfStops.entrySet()) {
            if(e.getKey().contains(busStopName) && !busStopName.equalsIgnoreCase("The")) {
                return e.getValue().getName();
            }
        }
        return null;
    }

    public String displayAllStops() {
        StringBuilder busStopString = new StringBuilder();
        for (Map.Entry<String, BusStop> entry : setOfStops.entrySet()) {
            busStopString.append(entry.getKey()).append("\n");
        }
        return busStopString.toString();
    }

    public BusStop getClosestBusStop(double latitude, double longitude) {
        BusStop closestBusStop = null;
        double shortestDistance = Double.MAX_VALUE;
        for (Map.Entry<String, BusStop> entry: setOfStops.entrySet()) {
            double currentDistance = DistanceGenerator.distance(latitude, longitude,
                    entry.getValue().getLatitude(), entry.getValue().getLongitude(), "K");
            if (currentDistance < shortestDistance) {
                shortestDistance = currentDistance;
                closestBusStop = entry.getValue();
            }
        }
        return closestBusStop;
    }

    public LinkedList<BusRoute> getBusRoutesForStop(BusStop busStop) {
        return routesWithBusStop(busStop.getName());
    }

    public LinkedList<BusRouteTimesWrapper> getBusRoutesForDay(BusStop busStop) {
        LinkedList<BusRoute> listOfDayRoutes = getBusRoutesForStop(busStop);
        LinkedList<BusRouteTimesWrapper> list = this.listOfRoutesForTime.get(getDayOfWeek());
        LinkedList<BusRouteTimesWrapper> filteredList = new LinkedList<>();
        for (BusRouteTimesWrapper brtw: list) {
            for (BusRoute br: listOfDayRoutes) {
                if (br.getRouteName().equals(brtw.getBusRouteName()) && !filteredList.contains(brtw)) {
                    filteredList.add(brtw);
                }
            }
        }
        return filteredList;
    }

    public String getDayOfWeek() {
        String[] days = new String[] { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };
        Date dateTime = new Date(System.currentTimeMillis());
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateTime);
        return days[cal.get(Calendar.DAY_OF_WEEK)-1];
    }
    public LocalTime getCurrentTime() {
        long timeStamp = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date date = new Date(timeStamp);
        return LocalTime.parse(sdf.format(date));
    }

    public boolean validBusRouteTime() {
        LocalTime testTime = getCurrentTime();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("H:m", Locale.ENGLISH);
        String dayOfWeek = getDayOfWeek();

        switch (dayOfWeek) {
            case "Saturday":
                return testTime.isAfter(LocalTime.parse("11:00", dtf)) &&
                        testTime.isBefore(LocalTime.parse("18:30", dtf));
            case "Friday":
                return testTime.isAfter(LocalTime.parse("07:00", dtf)) &&
                        testTime.isBefore(LocalTime.parse("17:30", dtf));
            case "Monday":
            case "Tuesday":
            case "Wednesday":
            case "Thursday":
                if (testTime.isAfter(LocalTime.parse("20:30", dtf)) &&
                    testTime.isBefore(LocalTime.parse("21:00", dtf))) {
                    return false;
                }
                return  testTime.isAfter(LocalTime.parse("07:00", dtf)) &&
                        testTime.isBefore(LocalTime.parse("22:20", dtf));
            default:
                return false;
        }
    }

    public BusRoute getRealTimeBusRoute(BusStop busStop) {
        String dayOfWeek = getDayOfWeek();

        LocalTime currentTime = getCurrentTime();
        LinkedList<BusRouteTimesWrapper> busRoutesForCurrentDay = getBusRoutesForDay(busStop);

        if (dayOfWeek.equals("Friday") || dayOfWeek.equals("Saturday")) {
            return setOfRoutes.get(busRoutesForCurrentDay.getFirst().getBusRouteName());
        } else {
            for (BusRouteTimesWrapper br : busRoutesForCurrentDay) {
                boolean inTimeFrame = currentTime.isAfter(br.getBusRouteStart()) &&
                        currentTime.isBefore(br.getBusRouteEnd());
                if (inTimeFrame) {
                    return setOfRoutes.get(br.getBusRouteName());
                }
            }
            return null;
        }
    }

    public void readBusStopCSVInfo(String filePath) throws IOException {
        setOfStops.clear();
        BufferedReader brBusStop =
                new BufferedReader(new FileReader(filePath));

        String line;
        while((line = brBusStop.readLine()) != null) {
            String[] busStop = line.split(",");
            BusStop bs = new BusStop(busStop[0].toUpperCase(Locale.ROOT),
                    Double.parseDouble(busStop[1]),
                    Double.parseDouble(busStop[2])
            );
            setOfStops.put(bs.getName(), bs);
        }
    }

    public void readBusRouteCSVInfo(String filePath) throws IOException {
        setOfRoutes.clear();
        BufferedReader brBusRoute =
                new BufferedReader(new FileReader(filePath));
        //Parse Bus Stop Information
        String line;
        //Parse Bus Route Information
        while((line = brBusRoute.readLine()) != null) {
            String[] busRoute = line.split(",");
            String routeNumber = busRoute[0];
            String routeName = busRoute[1];
            LinkedList<BusStop> routeOrder = new LinkedList<>();
            for (int i = 2; i < busRoute.length; i++) {
                if (setOfStops.containsKey(busRoute[i].toUpperCase(Locale.ROOT))) {
                    routeOrder.add(setOfStops.get(busRoute[i].toUpperCase(Locale.ROOT)));
                } else {
                    System.out.println(busRoute[i] + " not found in set.");
                }
            }
            BusRoute currentRoute = new BusRoute(routeOrder, routeNumber + ": " + routeName);
            setOfRoutes.put(routeNumber + ": " + routeName, currentRoute);
        }
    }

    public void readBusRouteTimeCSVInfo(String filePath) throws IOException {
        listOfRoutesForTime.clear();
        BufferedReader brTimes =
                new BufferedReader(new FileReader(filePath));
        //Parse Bus Stop Information
        String line;
        while ((line = brTimes.readLine()) != null) {
            String[] busRouteTimes = line.split(",");

            String dayOfWeek = busRouteTimes[0];
            String busRouteName = busRouteTimes[1];
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("H:m", Locale.ENGLISH);
            LocalTime startTime = LocalTime.parse(busRouteTimes[2], dtf);
            LocalTime endTime = LocalTime.parse(busRouteTimes[3], dtf);

            BusRouteTimesWrapper busRouteTimeInfo = new BusRouteTimesWrapper(busRouteName, startTime, endTime);

            LinkedList<BusRouteTimesWrapper> currentList = listOfRoutesForTime.get(dayOfWeek);

            if (currentList == null) {
                currentList = new LinkedList<>();
            }
            currentList.add(busRouteTimeInfo);
            listOfRoutesForTime.put(dayOfWeek, currentList);
        }
    }

}
