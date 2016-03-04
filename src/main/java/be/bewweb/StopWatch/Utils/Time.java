package be.bewweb.StopWatch.Utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Quentin on 15-02-16.
 */
public class Time {
    public static String convertLocalTimeToUTC(String localTime, String timeFormat, String resultTimeFormat) throws ParseException{
        SimpleDateFormat localParser = new SimpleDateFormat(timeFormat);
        localParser.setTimeZone(Calendar.getInstance().getTimeZone());
        Date localDate = localParser.parse(localTime);

        SimpleDateFormat utcParser = new SimpleDateFormat(resultTimeFormat);
        utcParser.setTimeZone(TimeZone.getTimeZone("UTC"));
        return utcParser.format(localDate);
    }

    /**
     * this function provide the timestamp of a utc time
     * @param utcTime UTC time dd/MM/yyyy HH:mm:ss
     * @return timestamp in milisecond
     */
    public static long getTimestamp(String utcTime, String timeFormat) throws ParseException {
        SimpleDateFormat utcParser = new SimpleDateFormat(timeFormat);
        utcParser.setTimeZone(TimeZone.getTimeZone("UTC"));
        return utcParser.parse(utcTime).getTime();
    }
    /**
     * this function provide the utc time of a timestamp (utc)
     * @param utcTime UTC time dd/MM/yyyy HH:mm:ss
     * @return timestamp in milisecond
     */
    public static String getUTCTime(long utcTime, String timeFormat) throws ParseException {
        SimpleDateFormat utcParser = new SimpleDateFormat(timeFormat);
        utcParser.setTimeZone(TimeZone.getTimeZone("UTC"));
        return utcParser.format(utcTime);
    }

    public static String convertUTCToLocalTime(String utcTime, String timeFormat, String resultTimeFormat) throws ParseException{
        SimpleDateFormat localParser = new SimpleDateFormat(timeFormat);
        localParser.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date utcDate = localParser.parse(utcTime);

        SimpleDateFormat utcParser = new SimpleDateFormat(resultTimeFormat);
        utcParser.setTimeZone(Calendar.getInstance().getTimeZone());
        return utcParser.format(utcDate);
    }


}
