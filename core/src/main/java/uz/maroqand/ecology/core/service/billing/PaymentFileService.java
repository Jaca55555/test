package uz.maroqand.ecology.core.service.billing;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.maroqand.ecology.core.entity.billing.PaymentFile;

import java.util.Date;
import java.util.List;

/**
 * Created by Utkirbek Boltaev on 23.07.2019.
 * (uz)
 * (ru)
 */
public interface PaymentFileService {

    PaymentFile getById(Integer id);

    List<PaymentFile> getByInvoice(String invoice);

    PaymentFile save(PaymentFile paymentFile);

    PaymentFile update(PaymentFile paymentFile, Integer userId);

    PaymentFile create(PaymentFile paymentFile);

    void removeInvoiceIsDublicate(PaymentFile paymentFile, Integer userId);

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
            String oldAccount,
            Pageable pageable
    );
}
