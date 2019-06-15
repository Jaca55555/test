package uz.maroqand.ecology.core.service.billing;

import uz.maroqand.ecology.core.entity.billing.Payment;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;

/**
 * Created by Utkirbek Boltaev on 15.06.2019.
 * (uz)
 * (ru)
 */
public interface PaymentService {

    Payment checkRegApplicationPaymentStatus(RegApplication regApplication);
}
