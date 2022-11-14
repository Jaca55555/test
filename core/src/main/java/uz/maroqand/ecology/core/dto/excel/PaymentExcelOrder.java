package uz.maroqand.ecology.core.dto.excel;

import lombok.Data;

import java.util.Date;

@Data
public class PaymentExcelOrder implements DocumentOrderParams {
    private Integer id;
    private String invoice;
    private String payer;
    private Integer tin;
    private Date paymentDate;
    private Double paymentSum;
    private String hr;
    private String reciever;
    private Integer recieverTin;
    private Integer recieverMFO;
    private String information;

    @Override
    public DocumentOrderType getType() {
        return null;
    }
}
