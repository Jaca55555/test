package uz.maroqand.ecology.core.service.expertise;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.maroqand.ecology.core.entity.expertise.Facture;

import java.util.Date;

public interface FactureService {

    Facture getById(Integer id);

    Page<Facture> findFiltered(
            Date dateBegin,
            Date dateEnd,
            Boolean dateToday,
            Boolean dateThisMonth,
            String invoice,
            String service,
            String detail,
            Integer regionId,
            Integer subRegionId,
            Integer payeeId,
            Pageable pageable
    );

}
