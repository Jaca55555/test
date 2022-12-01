package uz.maroqand.ecology.core.dto.api;

import lombok.Data;

/**
 *
 * Created by Sadullayev Akmal on 28.08.2020.
 */
@Data
public class Response {
    private String status;
    private String message;
    private String client;
    private String tin;
    private String amount;
    private String paidAmount;
    private String residualAmount;
    private String orgAccount;
    private String mfo;
}
