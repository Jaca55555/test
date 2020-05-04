package uz.maroqand.ecology.core.repository.billing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.core.entity.billing.InvoiceAudit;

import java.util.List;

/**
 * Created by Utkirbek Boltaev on 24.05.2018.
 * (uz)
 * (ru)
 */
@Repository
public interface InvoiceAuditRepository extends JpaRepository<InvoiceAudit, Integer> {

    List<InvoiceAudit> findByInvoiceId(Integer id);

}
