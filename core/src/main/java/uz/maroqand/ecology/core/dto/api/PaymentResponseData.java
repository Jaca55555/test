package uz.maroqand.ecology.core.dto.api;

import lombok.Data;

/**
 * Created by Utkirbek Boltaev on 20.11.2019.
 * (uz)
 * (ru)
 */
@Data
public class PaymentResponseData {

    private Integer id;
    private Integer eco_id;

    public PaymentResponseData(Integer id, Integer eco_id){
        this.id = id;
        this.eco_id = eco_id;
    }
}