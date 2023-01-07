package uz.maroqand.ecology.core.dto.id_egov;

import lombok.Data;

import java.util.List;

/**
 * Created by Utkirbek Boltaev on 12.11.2020.
 */
@Data
public class IdGovResponseDto {
    private List<IdGovLEResponseDto> legal_info;
    private String valid;
    private String tem_adr;
    private String role_list;
    private String le_name;
    private String gd;
    private String per_adr;
    private String pport_issue_date;
    private String sur_name;
    private String ctzn;
    private String first_name;
    private String le_tin;
    private String tin;
    private String user_id;
    private String birth_date;
    private String user_type;
    private String birth_cntry;
    private String pport_expr_date;
    private String ret_cd;
    private String natn;
    private String birth_place;
    private String ws_list;
    private String pport_no;
    private String mid_name;
    private String mob_phone_no;
    private String pin;
    private String email;
    private String sess_id;
    private String pport_issue_place;
    private String full_name;
    private String photo;

    /*
        {
          "legal_info": [
            {
              "is_basic": false,
              "tin": "305049488",
              "acron_UZ": "\"MAX IT\" MChJ"
            }
          ],
          "birth_date": "1992-04-10",
          "pport_no": "AA1124484",
          "ctzn": "УЗБЕКИСТАН",
          "per_adr": "ФАРҲОД МФЙ, 23 МАВЗЕ",
          "tin": "502003257",
          "pport_issue_place": "ГИЖДУВАНСКИЙ ГРОВД БУХАРСКОЙ ОБЛАСТИ",
          "birth_place_id": "",
          "sur_name": "SHUKUROV",
          "gd": "1",
          "citizenship_id": "182",
          "natn": "УЗБЕК/УЗБЕЧКА",
          "pport_issue_date": "2013-26-03",
          "pport_expr_date": "2023-25-03",
          "doc_give_place_id": "6224",
          "pin": "31004925310011",
          "mob_phone_no": "+998909120107",
          "user_id": "alijon",
          "email": "alijonshukurov@gmail.com",
          "birth_place": "G‘IJDUVON SHAHRI",
          "mid_name": "OLIMOVICH",
          "nationality_id": "44",
          "valid": "false",
          "user_type": "L",
          "sess_id": "a8ef3e44-1080-44ee-8038-90b7abba5500",
          "ret_cd": "0",
          "first_name": "ALIJON",
          "full_name": "ALIJON OLIMOVICH SHUKUROV"
        }
    * */
    /*
    * {
    * "valid":"false",
    * "gd":"1",
    * "per_adr":"УРГАНЧ Ш., ИСТИКЛОЛ МФЙ, АЛ- ХОРАЗМИЙ",
    * "pport_issue_date":"2016-11-05",
    * "sur_name":"BOLTAYEV",
    * "ctzn":"УЗБЕКИСТАН",
    * "first_name":"UTKIRBEK",
    * "tin":"556410450",
    * "user_id":"utkir",
    * "birth_date":"1993-04-27",
    * "user_type":"I",
    * "birth_cntry":"УЗБЕКИСТАН",
    * "pport_expr_date":"2026-10-05",
    * "natn":"УЗБЕК/УЗБЕЧКА",
    * "birth_place":"QO‘SHKO‘PIR TUMANI",
    * "pport_no":"AB3860558",
    * "mid_name":"O‘KTAMOVICH",
    * "mob_phone_no":"+998937438468",
    * "pin":"32704933090022",
    * "email":"utkirbek.boltaev@gmail.com",
    * "sess_id":"0f830ce7-2da4-4744-b17b-2a7ab4ee1572",
    * "pport_issue_place":"УРГЕНЧСКИЙ ГОВД ХОРЕЗМСКОЙ ОБЛАСТИ",
    * "full_name":"BOLTAYEV UTKIRBEK O‘KTAMOVICH"
    * }
    * */

    /*
    * {"valid":"false",
    * "tem_adr":"ТОШКЕНТ ШАЋРИ ЮНУСОБОД ТУМАНИ 4- МАССИВИ",
    * "role_list":"",
    * "le_name":"",
    * "gd":"M",
    * "per_adr":"ХОРАЗМ ВИЛОЯТИ УРГАНЧ ШАҲРИ АЛ-ХОРАЗМИЙ КЎЧАСИ 41-9",
    * "pport_issue_date":"20160511",
    * "sur_name":"BOLTAYEV",
    * "ctzn":"УЗБЕКИСТАН",
    * "first_name":"O\u2018TKIRBEK",
    * "le_tin":"",
    * "user_id":"utkir",
    * "birth_date":"19930427",
    * "user_type":"I",
    * "birth_cntry":"УЗБЕКИСТАН",
    * "pport_expr_date":"20260510",
    * "ret_cd":"0",
    * "natn":"УЗБЕК/УЗБЕЧКА",
    * "birth_place":"ХОРЕЗМСКИЙ ВИЛОЯТ",
    * "ws_list":"",
    * "pport_no":"AB3860558",
    * "mid_name":"O\u2018KTAMOVICH",
    * "mob_phone_no":"998937438468",
    * "pin":"32704933090022",
    * "email":"utkirbek.boltaev@gmail.com",
    * "sess_id":"db7c12c3a18ecc0a401159bb4cc059c8ea671968fb7f1d4839920c76b8c2bc06ae4ed6332f86e8540b8f2017494cc8610d4ddaf18f933d94a0abab2efffdba82",
    * "pport_issue_place":"УРГЕНЧСКИЙ ГОВД ХОРЕЗМСКОЙ ОБЛАСТИ",
    * "full_name":"BOLTAYEV O\u2018TKIRBEK O\u2018KTAMOVICH"}
    * */
}