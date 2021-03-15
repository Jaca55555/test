package uz.maroqand.ecology.core.util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

/**
 * Created by Utkirbek Boltaev on 27.09.2018.
 * (uz)
 * (ru)
 */
public class Common {

    public static SimpleDateFormat uzbekistanDateFormat = new SimpleDateFormat("dd.MM.yyyy");
    public static SimpleDateFormat uzbekistanDateFormatUz = new SimpleDateFormat("MM/dd/yyyy");
    public static SimpleDateFormat uzbekistanDateAndTimeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    public static SimpleDateFormat uzbekistanDateAndTimeFormatBank = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    private static String pattern = "######.###";
    public static  DecimalFormat decimalFormat = new DecimalFormat(pattern);
    public static final String RESPONSE_OK = "OK";

}
