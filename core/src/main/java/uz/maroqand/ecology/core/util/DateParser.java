package uz.maroqand.ecology.core.util;


import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Utkirbek Boltaev on 27.09.2018.
 * (uz)
 * (ru)
 */
public class DateParser {

    /**
     * Try to parse the date from the string
     *
     * @param dateAsString date in String
     * @param dateFormat DateFormat instance
     * @return Returns Date if successfull or null on failure.
     */
    public static Date TryParse(String dateAsString, DateFormat dateFormat) {
        try{
            return dateFormat.parse(dateAsString);
        } catch (Exception ignored) {
            return null;
        }
    }

    public static Date tryParseOrGetTomorrow(String dateAsString, DateFormat dateFormat) {
        Date result = null;
        if (!StringUtils.isEmpty(dateAsString)) {
            result = DateParser.TryParse(dateAsString, dateFormat);
        }
        if (result == null) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            result = calendar.getTime();
        }
        return result;
    }

    public static Date hourStringToDate(Date date , String stringDate){
        String[] splitDate = stringDate.split(":");
        int hour = Integer.parseInt(splitDate[0]);
        int min = Integer.parseInt(splitDate[1]);
        Long milliSecond = date.getTime() +(hour*60*60 + min*60)*1000;
        return new Date(milliSecond);
    }

}

