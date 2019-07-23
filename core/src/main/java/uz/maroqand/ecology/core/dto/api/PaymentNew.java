package uz.maroqand.ecology.core.dto.api;

import lombok.Data;

/**
 * Created by Utkirbek Boltaev on 23.07.2019.
 * (uz)
 * (ru)
 */
@Data
public class PaymentNew {

    private PaymentPayer payer;     //Тўловчини
    private PaymentBank bank;       //Тўловчининг банки

    private String amount;          //Тўлов суммаси
    private String document_number; //Документ рақами
    private String payment_date;    //Тўлов санаси ва вақти
    private String details;         //Тўлов топшириқномаси

}