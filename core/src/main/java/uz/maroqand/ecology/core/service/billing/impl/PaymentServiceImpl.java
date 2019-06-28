package uz.maroqand.ecology.core.service.billing.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.entity.billing.Payment;
import uz.maroqand.ecology.core.repository.billing.PaymentRepository;
import uz.maroqand.ecology.core.service.billing.PaymentService;

import java.util.List;

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

    public Payment pay(Payment payment){
        return paymentRepository.save(payment);
    }

    @Override
    public List<Payment> findAllByInvoiceId(Integer invoiceId){
        return paymentRepository.findAllByInvoiceIdAndDeletedFalse(invoiceId);
    }


}