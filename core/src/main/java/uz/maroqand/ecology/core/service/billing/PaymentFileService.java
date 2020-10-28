package uz.maroqand.ecology.core.service.billing;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.maroqand.ecology.core.entity.billing.PaymentFile;

import java.util.Date;

/**
 * Created by Utkirbek Boltaev on 23.07.2019.
 * (uz)
 * (ru)
 */
public interface PaymentFileService {

    PaymentFile getById(Integer id);

    PaymentFile save(PaymentFile paymentFile);

    PaymentFile update(PaymentFile paymentFile, Integer userId);

    PaymentFile create(PaymentFile paymentFile);
    PaymentFile checkAndCreateOrNotCreate(PaymentFile paymentFile);

    Page<PaymentFile> findFiltered(
            Date dateBegin,
            Date dateEnd,
            String invoice,
            Integer paymentId,

            Integer payerTin,
            String payerName,
            String detail,
            String bankMfo,

            Boolean isComplete,
            String account,
            Pageable pageable
    );
}
