package uz.maroqand.ecology.core.service.expertise;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.maroqand.ecology.core.entity.billing.Invoice;
import uz.maroqand.ecology.core.entity.client.Client;
import uz.maroqand.ecology.core.entity.expertise.Facture;
import uz.maroqand.ecology.core.entity.expertise.FactureProduct;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;
import uz.maroqand.ecology.core.entity.expertise.Requirement;
import uz.maroqand.ecology.core.entity.sys.Organization;

import java.util.Date;
import java.util.List;

/**
 * Created by Utkirbek Boltaev on 27.01.2010.
 * (uz)
 */
public interface FactureService {

    Facture create(
            RegApplication regApplication,
            Client client,
            Organization organization,
            Requirement requirement,
            Invoice invoice,
            String locale
    );

    Facture getById(Integer id);

    List<FactureProduct> getByFactureId(Integer factureid);

    Page<Facture> findFiltered(
            Date dateBegin,
            Date dateEnd,
            String number,
            String payerName,
            String payerTin,
            String payeeName,
            String payeeTin,
            Pageable pageable
    );

}
