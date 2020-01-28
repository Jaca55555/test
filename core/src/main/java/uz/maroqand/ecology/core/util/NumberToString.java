package uz.maroqand.ecology.core.util;

import org.springframework.stereotype.Service;

@Service
public class NumberToString {

   /* public static void main(String[] args) {
        Double son=850605.0;
        String numberName = numberToStringTranslation(son,"ru");
        System.out.println(son.toString() + " --> " + numberName);
        Random rand = new Random();
        for (int i=1; i<=10; i++){
            Double sonRand=rand.nextDouble()*2000*1000000000+1;
        System.out.println(sonRand.longValue() + " -->Uz= " + numberToStringTranslation(sonRand,"uz"));
        System.out.println(sonRand.longValue() + " -->RU= " + numberToStringTranslation(sonRand,"ru"));
        System.out.println(sonRand.longValue() + " -->En= " + numberToStringTranslation(sonRand,"en"));
            System.out.println();
        }

    }*/

    public  String numberToStringTranslation(Double numberDouble, String locale){
        System.out.println("numberDouble="+numberDouble);
        System.out.println("locale="+locale);
        if (numberDouble!=null){
            if (numberDouble<0)numberDouble*=(-1);
            Long number= numberDouble.longValue();
            String numberStr=number.toString();
            int numberStrLength=numberStr.length();
            String numberName="";

                //uzb
            if (locale.equals("uz")){

                if (number==0){
                    return "no`l";
                }
                String [] birlar = {"","bir","ikki","uch","to`rt","besh","olti","yetti","sakkiz","to`qqiz"};
                String [] unlar = {"","o`n","yigirma","o`ttiz","qirq","ellik","oltmish","yetmish","sakson","to'qson"};
                String [] yuzlar = {"yuz","ming","million","milliard","trillion","kvadrillion","kvintillion"};
                int key1=1;
                int key2=0;
                for (int i=numberStrLength; i>0; i--,key1++){
                    int index=Integer.parseInt(numberStr.substring(i-1,i));
                    int index2 = i!=1 ? Integer.parseInt(numberStr.substring(i-2,i-1)) : 0;
                        if (key1 == 1) {
                            numberName = birlar[index] + " " + numberName.trim();
                        } else {
                            if (key1 == 2) {
                                numberName = unlar[index] + " " + numberName.trim();
                            } else {
                                if (index!=0){
                                    numberName = birlar[index] + " " + yuzlar[0] + " " + numberName.trim();
                                }
                            }
                        }
                        if (key1 == 3 && i != 1) {
                            key2++;
                            key1 = 0;
                            if (index2 != 0 || i>2) {
                                numberName = yuzlar[key2] + " " + numberName.trim();
                            }
                        }
                }
                return numberName;
            }else if (locale.equals("oz")){{
                if (number==0){
                    return "нул";
                }
                String [] birlar = {"","бир","икки","уч","турт","беш","олти","йэтти","саккиз","туккиз"};
                String [] unlar = {"","ун","йигирма","уттиз","кирк","еллик","олтмиш","йетмиш","саксон","туксон"};
                String [] yuzlar = {"юз","минг","миллион","миллиард","триллион","евадратилион","квинтилион"};
                int key1=1;
                int key2=0;
                for (int i=numberStrLength; i>0; i--,key1++){
                    int index=Integer.parseInt(numberStr.substring(i-1,i));
                    int index2 = i!=1 ? Integer.parseInt(numberStr.substring(i-2,i-1)) : 0;
                    if (key1 == 1) {
                        numberName = birlar[index] + " " + numberName.trim();
                    } else {
                        if (key1 == 2) {
                            numberName = unlar[index] + " " + numberName.trim();
                        } else {
                            if (index!=0){
                                numberName = birlar[index] + " " + yuzlar[0] + " " + numberName.trim();
                            }
                        }
                    }
                    if (key1 == 3 && i != 1) {
                        key2++;
                        key1 = 0;
                        if (index2 != 0 || i>2) {
                            numberName = yuzlar[key2] + " " + numberName.trim();
                        }
                    }
                }
                return numberName;
            }
            }else{
                    //rus
                if (locale.equals("ru")){

                    if (number==0){
                        return "нол";
                    }
                    /*                       1      2     3      4       5      6       7      8        9*/
                    String [] birlar = {"","один","два","три","четыре","пять","шесть","семь","восемь","девять"};
                    /*                       11             12          13            14            15            16            17           18             19*/
                    String [] unlar = {"","одиннадцать","двенадцать","тринадцать","четырнадцать","пятнадцать","шестнадцать","семнадцать","восемнадцать","девятнадцать"};
                    /*                           10        20           30       40      50          60            70         80            90  */
                    String [] kattaUnlar = {"","десять","двадцать","тридцать","сорок","пятьдесят","шестьдесят","семьдесят","восемьдесят","девяносто"};
                    /*                      100    200       300       400        500        600       700        800        900*/
                    String [] yuzlar = {"","сто","двести", "триста","четыреста","пятьсот","шестьсот","семьсот","восемьсот","девятьсот"};
                    /*                          1000    1000000   ....*/
                    String [] millionlar = {"","тысяч","миллион","миллиард","триллион","квадрильон","квинтильон"};

                    if (numberStrLength==1){
                        return birlar[Integer.parseInt(numberStr)-1];
                    }
                    int key1=1;
                    int key2=0;

                    for (int i=numberStrLength; i>0; i--,key1++){

                        int index = Integer.parseInt(numberStr.substring(i-1,i));
                        int index2 = i!=1 ? Integer.parseInt(numberStr.substring(i-2,i-1)) : 0;

                        if (key1==1){
                            if (index2 == 1 && index != 0){
                                numberName = unlar[index] + " " + numberName.trim();
                                key1++;
                                i--;
                                continue;
                            }else{
                            numberName=birlar[index]+ " " + numberName.trim();
                            }
                        }else{
                            if (key1==2){
                                numberName = kattaUnlar[index] + " " + numberName.trim();
                            }
                            else {
                                if (index!=0) {
                                    numberName=yuzlar[index]+ " " + numberName.trim();
                                }
                            }
                        }

                        if (key1==3 && i!=1){
                            key2++;
                            key1=0;
                            String million ="";
                            if (index2==1 && key2==1){
                                million = "одна ";
                                key1++;
                                i--;
                            }else{
                                if (index2==2 && key2==1){
                                    million = "две ";
                                    key1++;
                                    i--;
                                }
                            }
                            if (index2!=0 || i>2) {
                                numberName=million + millionlar[key2] + getSuffixesRu(key2,index2) + " " + numberName.trim();
                            }
                        }
                    }
                    return numberName.trim();
                }else{
                    //engl
                    if (locale.equals("en")){
                        if (number==0){
                            return "nol";
                        }
                            /*                  1     2      3      4      5       6     7      8       9      10    11       12        13         14        15        16         17            18        19      */
                        String [] birlar = {"","one","two","three","four","five","six","seven","eight","nine","ten","eleven","twelve","thirteen","fourteen","fifteen","sixteen","seventeen","eighteen","nineteen",""};
                        /*                          20       30      40      50      60       70      80        90  */
                        String [] unlar = {"","","twenty","thirty","forty","fifty","sixty","seventy","eighty","ninety"};
                        /*                     100       1000     1000000  1000000000  .........*/
                        String [] yuzlar = {"hundred","thousand","million","billion","trillion","quadrillion","quintillion"};

                        if (numberStrLength==1){
                            return birlar[Integer.parseInt(numberStr)-1];
                        }
                        int key1=1;
                        int key2=0;

                        for (int i=numberStrLength; i>0;i--,key1++){
                            int index = Integer.parseInt(numberStr.substring(i-1,i));
                            int index2 = i!=1 ? Integer.parseInt(numberStr.substring(i-2,i-1)) : 0;

                            if (key1==1){
                                if (index2 == 1){
                                    numberName=birlar[10+index]+ " " + numberName.trim();
                                    key1++;
                                    i--;
                                    continue;
                                }else{
                                    numberName=birlar[index]+ " " + numberName.trim();
                                }
                            }else{
                                if (key1==2){
                                    numberName=unlar[index] + " " + numberName.trim();
                                }
                                else {
                                    if (index!=0){
                                        numberName=birlar[index]+ " " + yuzlar[0]+ " " + numberName.trim();
                                    }
                                }
                            }

                            if (key1==3 && i!=1){
                                key2++;
                                key1=0;
                                if (index2!=0  || i>2){
                                    numberName=yuzlar[key2] + " " + numberName.trim();
                                }
                            }
                        }
                        return numberName.trim();
                    }else {
                        return  null;
                    }
                }
            }
        }else{
            return null;
        }
    }

    private  String getSuffixesRu (int key2,int number){
        if (key2==1){
            if (number==1){
                return "а";
            }
            if(number > 1 && number < 5){
                return "и";
            }
            return "";
        }else{
            if (number==1){
                return "";
            }
            if (number >1 && number < 5){
                return "а";
            }
            return "ов";
        }
    }
}
