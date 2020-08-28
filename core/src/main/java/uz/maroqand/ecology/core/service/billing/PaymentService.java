package uz.maroqand.ecology.core.service.billing;

import uz.maroqand.ecology.core.constant.billing.PaymentType;
import uz.maroqand.ecology.core.entity.billing.Invoice;
import uz.maroqand.ecology.core.entity.billing.Payment;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Utkirbek Boltaev on 15.06.2019.
 * (uz)
 * (ru)
 */
public interface PaymentService {

    Payment pay(Integer invoiceId, Double amount, Date paymentDate, String detail, PaymentType paymentType);

    List<Payment> getByInvoiceId(Integer id);

    Map<String,Object> sendSmsPaymentAndGetResponseMap(
            Invoice invoice,
            String telephone,
            String cardNumber,
            String cardMonth,
            String cardYear,
            String successUrl,
            String failUrl
    );

    Map<String, Object> confirmSmsAndGetResponseAsMap(
            Integer applicationId,
            Integer paymentId,
            Integer tempTransId,
            String confirmSms,
            String successUrl,
            String failUrl
    );

    Payment save(Payment payment);

}
