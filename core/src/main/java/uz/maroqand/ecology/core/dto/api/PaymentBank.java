package uz.maroqand.ecology.core.dto.api;

import lombok.Data;

/**
 * Created by Utkirbek Boltaev on 23.07.2019.
 * (uz)
 * (ru)
 */
@Data
public class PaymentBank {
    private String account; //Тўловчини ҳисоб рақами
    private String mfo;     //Тўловчининг банк МФОси
}
