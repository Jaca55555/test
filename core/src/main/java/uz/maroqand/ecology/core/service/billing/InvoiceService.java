package uz.maroqand.ecology.core.service.billing;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.maroqand.ecology.core.constant.billing.InvoiceStatus;
import uz.maroqand.ecology.core.entity.billing.Invoice;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;
import uz.maroqand.ecology.core.entity.expertise.Requirement;

import java.util.Date;
import java.util.List;

/**
 * Created by Utkirbek Boltaev on 15.06.2019.
 * (uz)
 * (ru)
 */
public interface InvoiceService {

    Invoice create(RegApplication regApplication, Requirement requirement);

    Invoice modification(RegApplication regApplication, Invoice invoice, Requirement requirement);

    Invoice getInvoice(Integer id);

    Invoice getInvoice(String invoice);

    Invoice payTest(Integer id);

    List<Invoice> getListByStatus(InvoiceStatus invoiceStatus);

    List<Invoice> getListByStatusAndClientId(InvoiceStatus invoiceStatus,Integer clientId);

    Invoice cancelInvoice(Invoice invoice);

    Page<Invoice> findFiltered(
      Date dateBegin,
      Date dateEnd,
      Boolean dateToday,
      Boolean dateThisMonth,
      InvoiceStatus status,
      String invoice,
      String service,
      String detail,
      Integer regionId,
      Integer subRegionId,
      Integer payeeId,
      Pageable pageable
    );

}
