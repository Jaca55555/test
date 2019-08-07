package uz.maroqand.ecology.core.util;

/**
 * Created by Utkirbek Boltaev on 07.08.2019.
 * (uz)
 * (ru)
 */
public class Parser {

    public static Integer stringToInteger(String val){
        try {
            return Integer.parseInt(val);
        }catch (Exception e){
            return null;
        }
    }

}
