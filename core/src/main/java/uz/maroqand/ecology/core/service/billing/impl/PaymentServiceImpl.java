package uz.maroqand.ecology.core.service.billing.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.constant.billing.PaymentStatus;
import uz.maroqand.ecology.core.entity.billing.Payment;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;
import uz.maroqand.ecology.core.repository.billing.PaymentRepository;
import uz.maroqand.ecology.core.service.billing.PaymentService;

/**
 * Created by Utkirbek Boltaev on 15.06.2019.
 * (uz)
 * (ru)
 */

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public Payment checkRegApplicationPaymentStatus(RegApplication regApplication) {
        Payment payment = new Payment();
        payment.setStatus(PaymentStatus.Initial);
        //todo shart kerak
        /*if (regApplication.getInvoiceId()!=null){

        }*/
        return payment;
    }
}