package uz.maroqand.ecology.core.dto.api;

import lombok.Data;

/**
 * Created by Utkirbek Boltaev on 17.01.2020.
 * (uz)
 * (ru)
 */
@Data
public class PaymentInfoDto {
    private String bank;
    private String amount;
    private String document_number;
    private String mfo;
    private Integer tin;
    private String name;
    private String details;
    private Integer id;
    private String payer;
    private String account;
    private String payment_date;
}
