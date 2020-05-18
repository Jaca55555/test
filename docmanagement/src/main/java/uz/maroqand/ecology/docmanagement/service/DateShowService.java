package uz.maroqand.ecology.docmanagement.service;

import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

@Service
public class DateShowService {
    public String getUzmonth(int num){

        switch (num){
            case 1:
                return  "Yanvar";
            case 2:
                return  "Fevral";
            case 3:
                return  "Mart";
            case 4:
                return  "Aprel";
            case 5:
                return  "May";
            case 6:
                return  "Iyun";
            case 7:
                return  "Iyul";
            case 8:
                return  "Avgust";
            case 9:
                return  "Sentyabr";
            case 10:
                return   "Oktyabr";
            case 11:
                return  "Noyabr";
            case 12:
                return  "Dekabr";
            default:return "";
        }

    }
    public String getRumonth(int num){

        switch (num){
            case 1:
                return  "Январь";
            case 2:
                return  "Февраль";
            case 3:
                return  "Март";
            case 4:
                return  "Апрель";
            case 5:
                return  "Май";
            case 6:
                return  "Июнь";
            case 7:
                return  "Июль";
            case 8:
                return  "Август";
            case 9:
                return  "Сентябрь";
            case 10:
                return   "Октябрь";
            case 11:
                return  "Ноябрь";
            case 12:
                return "Декабрь";
            default:return "";
        }

    }
    public String getCurrentTimeUsingDate(String locale) {
        Calendar cal = Calendar.getInstance();
        int num = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);
        int dom = cal.get(Calendar.DAY_OF_MONTH);
        switch (locale) {
            case "ru":
                return dom+" "+getRumonth(num)+" "+year+" года.";
            case "oz":
                return dom+" "+getRumonth(num)+" "+year+" йил.";
            default:
                return dom+" "+getUzmonth(num)+" "+year+" yil.";
        }

    }
}
