package uz.maroqand.ecology.core.service.expertise;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.maroqand.ecology.core.entity.expertise.Offer;

import java.util.List;

/**
 * Created by Utkirbek Boltaev on 15.06.2019.
 * (uz)
 * (ru)
 */
public interface OfferService {

    Offer getOffer(Boolean budget,Integer organizationId);

    Page<Offer> getAll(Pageable pageable);
    Offer getByDocumentRepoId(Integer repoId);
    Offer getById(Integer id);

    Offer save(Offer offer);

    List<Offer> getAllByLanguage();
    Offer complete(Integer offerId);
    Integer getOfferFileIdByLanguage(Offer offer,String locale);

}
