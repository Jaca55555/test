package uz.maroqand.ecology.core.dto.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String receiver_mfo;          //MFO poluchatelya
    private Integer receiver_inn;          //INN poluchatelya
    private String receiver_name;          //Naimenovaniye poluchatelya
    private String receiver_acc;          //Schet poluchatelya
    private String document_number; //Документ рақами
    private String payment_date;    //Тўлов санаси ва вақти
    private String details;         //Тўлов топшириқномаси
    private Long id;

}