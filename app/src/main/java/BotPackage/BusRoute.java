package BotPackage;

import java.util.List;
import java.util.Objects;

public class BusRoute {
    private final List<BusStop> listOfStops;
    private final String routeName;


    public BusRoute(List<BusStop> listOfStops, String routeName) {
        this.listOfStops = listOfStops;
        this.routeName = routeName;
    }

    public List<BusStop> getListOfStops() {
        return listOfStops;
    }

    public String getRouteName() {
        return routeName;
    }

    public boolean containsBusStop(String busStopName) {
        for (BusStop stop: listOfStops) {
            if (stop.getName().equals(busStopName)) {
                return true;
            }
        }
        return false;
    }

    public void printStopsOrder() {
        for (BusStop b : listOfStops) {
            if (Objects.equals(listOfStops.get(listOfStops.size()-1) .getName(), b.getName())) {
                System.out.print(b.getName());
            } else {
                System.out.print(b.getName() + "-->");
            }
        }
        System.out.println();
    }
}
