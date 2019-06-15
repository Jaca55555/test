package uz.maroqand.ecology.core.service.billing;

import uz.maroqand.ecology.core.entity.billing.Invoice;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;
import uz.maroqand.ecology.core.entity.expertise.Requirement;

/**
 * Created by Utkirbek Boltaev on 15.06.2019.
 * (uz)
 * (ru)
 */
public interface InvoiceService {

    Invoice create(RegApplication regApplication, Requirement requirement);

    Invoice getInvoice(Integer id);

    Invoice payTest(Integer id);

}
