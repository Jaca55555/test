package uz.maroqand.ecology.core.repository.billing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.core.constant.billing.InvoiceStatus;
import uz.maroqand.ecology.core.entity.billing.Invoice;

import java.util.List;

/**
 * Created by Utkirbek Boltaev on 15.06.2019.
 * (uz)
 * (ru)
 */
@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Integer>, JpaSpecificationExecutor<Invoice> {

    Invoice findByIdAndDeletedFalse(Integer id);

    Invoice findByInvoiceAndDeletedFalse(String id);

    List<Invoice> findAllByStatusAndDeletedFalse(InvoiceStatus invoiceStatus);

}
