package uz.maroqand.ecology.core.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Anvar Ruzmetov on 16.03.2017.
 */
public class FixedSymbolOperation {

    public static String getOnlyDigits(String data){
        Pattern pattern = Pattern.compile("[^0-9]");
        Matcher matcher = pattern.matcher(data);
        String number = matcher.replaceAll("");
        return  number;
    }
}
