package uz.maroqand.ecology.core.repository.billing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.core.entity.billing.PaymentFile;

import java.util.Date;

/**
 * Created by Utkirbek Boltaev on 23.07.2019.
 * (uz)
 * (ru)
 */
@Repository
public interface PaymentFileRepository extends JpaRepository<PaymentFile, Integer>,JpaSpecificationExecutor<PaymentFile> {

    PaymentFile findByAmountAndPaymentDateAndPayerTin(Double amount, Date paymentDate, Integer payerTin);

}
