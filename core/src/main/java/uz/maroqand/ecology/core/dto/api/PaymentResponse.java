package uz.maroqand.ecology.core.dto.api;

import lombok.Data;

import java.util.List;

/**
 * Created by Utkirbek Boltaev on 23.07.2019.
 * (uz)
 * (ru)
 */
@Data
public class PaymentResponse {
    private String code;
    private String message;
    private List<PaymentResponseData> data;
}
