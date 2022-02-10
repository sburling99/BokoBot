package BotPackage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandListUtil {
    private static final String allCommands = "commands waddup hmmm hippity bday sqlTest neighbor busStopGPS allBusStops";

    public static List<String> commandsAsList() {
        return new ArrayList<>(
                Arrays.asList(CommandListUtil.allCommands.split(" "))
        );
    }
}
