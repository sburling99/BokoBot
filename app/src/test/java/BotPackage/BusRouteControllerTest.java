package BotPackage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;

class BusRouteControllerTest {
    BusRouteController brc;

    @BeforeEach
    public void setUp() {
        brc = new BusRouteController(new TreeMap<>(),new TreeMap<>(),new TreeMap<>());
        try {
            brc.readBusStopCSVInfo("./src/main/data/BusStopInfo.csv");
            brc.readBusRouteCSVInfo("./src/main/data/BusRouteInfo.csv");
            brc.readBusRouteTimeCSVInfo("./src/main/data/BusRouteTimes.csv");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void displayBusStopGPS() {
        assertEquals(brc.getClosestBusStop(29.903165, -97.900210).getName(), "GROVE");
    }


    @Test
    void containsBusStop() {
        assertTrue(brc.containsBusStop("ALGARITA"));
    }

    @Test
    void displayAllStops() {
        String allBusStops = "ALGARITA\nBISHOP SQUARE\nBOBCAT STADIUM EAST\nBOBCAT STADIUM WEST\nCABANA BEACH\n" +
                "CASTLE ROCK\nCOPPER BEECH\nDAKOTA RANCH\nELEVATION\nGROVE\nHEIGHTS II\nHIGHCREST\nHILLSIDE RANCH\n" +
                "HOLLAND\nINGRAM\nJAMES STREET\nLATANA\nLBJ STUDENT CENTER\nLYNDON\nMATTHEWS STREET LOT\nMILLS STREET LOT\n" +
                "OLD MILL\nPALAZZO\nPRIME OUTLET\nQUAD\nREC CENTER\nRETREAT\nRIVER OAKS VILLAS\nRIVERSIDE RANCH\n" +
                "SESSOM\nSEWELL\nSPECK GARAGE\nSPRING LAKE FIELDS\nSPRING MARC\nSUMMIT\nTARGET\nTELLURIDE\nTHE EDGE\n" +
                "THE OUTPOST\nTOWER\nUAC\nUEC\nUNIVERSITY CLUB\nUPTOWN SQUARE\nVERANDAH\nVILLAGE GREEN\nWATER TOWER\n" +
                "WEST AVE\nWOODS STREET\n";
        assertEquals(brc.displayAllStops(), allBusStops);
    }

    @Test
    void getClosestBusStop() {
        assertEquals(brc.getClosestBusStop(29.890751826607985, -97.94136608465949).getName(), "QUAD");
    }

    @Test
    void getBusRoutesForStop() {
        LinkedList<String> shouldBeBusRoute = new LinkedList<>();
        LinkedList<String> actualBusRoute = new LinkedList<>();
        shouldBeBusRoute.add("Route 24: Bishop Square");
        shouldBeBusRoute.add("Route 44: Ranch Road/Craddock/Holland");
        shouldBeBusRoute.add("Route 54: Night West");
        shouldBeBusRoute.add("Route 67: San Marcos Circulator");
        for (BusRoute br: brc.getBusRoutesForStop(brc.getClosestBusStop(29.898227,-97.961861))) {
            actualBusRoute.add(br.getRouteName());
        }
        assertArrayEquals(actualBusRoute.toArray(), shouldBeBusRoute.toArray());
    }


    @Test
    void getDayOfWeek() {
        String[] days = new String[] { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };
        Date dateTime = new Date(System.currentTimeMillis());
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateTime);
        assertEquals(days[cal.get(Calendar.DAY_OF_WEEK)-1], brc.getDayOfWeek());
    }

    @Test
    void getCurrentTime() {
        long timeStamp = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date date = new Date(timeStamp);
        LocalTime lt = LocalTime.parse(sdf.format(date));
        assertEquals(lt, brc.getCurrentTime());
    }

    @Test
    void validBusRouteTime() {
        assertTrue(brc.validBusRouteTime());
    }

}