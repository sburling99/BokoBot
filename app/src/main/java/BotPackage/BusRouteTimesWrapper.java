package BotPackage;

import java.time.LocalTime;

public class BusRouteTimesWrapper {
    private final String busRouteName;
    private final LocalTime busRouteStart;
    private final LocalTime busRouteEnd;

    public String getBusRouteName() {
        return busRouteName;
    }

    public LocalTime getBusRouteStart() {
        return busRouteStart;
    }

    public LocalTime getBusRouteEnd() {
        return busRouteEnd;
    }

    public BusRouteTimesWrapper(String busRouteName, LocalTime busRouteStart, LocalTime busRouteEnd) {
        this.busRouteName = busRouteName;
        this.busRouteStart = busRouteStart;
        this.busRouteEnd = busRouteEnd;
    }
}
