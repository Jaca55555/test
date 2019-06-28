package uz.maroqand.ecology.core.service.billing;

import uz.maroqand.ecology.core.entity.billing.Payment;

import java.util.List;

/**
 * Created by Utkirbek Boltaev on 15.06.2019.
 * (uz)
 * (ru)
 */
public interface PaymentService {

    Payment pay(Payment payment);

    List<Payment> findAllByInvoiceId(Integer invoiceId);

}
