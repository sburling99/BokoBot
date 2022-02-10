package BotPackage;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatter {
    public static String getDate () {
        SimpleDateFormat s = new SimpleDateFormat("MM/dd");
        String date = s.format(new Date());
        return date;
    }
}