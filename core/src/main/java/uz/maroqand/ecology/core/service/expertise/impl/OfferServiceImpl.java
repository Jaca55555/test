package uz.maroqand.ecology.core.service.expertise.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.constant.sys.DocumentRepoType;
import uz.maroqand.ecology.core.entity.expertise.Offer;
import uz.maroqand.ecology.core.entity.sys.DocumentRepo;
import uz.maroqand.ecology.core.repository.expertise.OfferRepository;
import uz.maroqand.ecology.core.service.expertise.OfferService;
import uz.maroqand.ecology.core.service.sys.DocumentRepoService;

import java.util.List;

/**
 * Created by Utkirbek Boltaev on 15.06.2019.
 * (uz)
 * (ru)
 */
@Service
public class OfferServiceImpl implements OfferService {

    private final OfferRepository offerRepository;
    private final DocumentRepoService documentRepoService;
    @Autowired
    public OfferServiceImpl(OfferRepository offerRepository, DocumentRepoService documentRepoService) {
        this.offerRepository = offerRepository;
        this.documentRepoService = documentRepoService;
    }

    @Override
    public Offer getOffer(Boolean budget,Integer organizationId){
        return offerRepository.findTop1ByActiveTrueAndByudjetAndOrganizationIdOrderByIdDesc(budget,organizationId);
    }

    @Override
    public Page<Offer> getAll(Pageable pageable){
        return offerRepository.findAllByActiveTrueAndDeletedFalse(pageable);
    }

    @Override
    public Offer getByDocumentRepoId(Integer repoId) {
        return offerRepository.findByDocumentRepoIdAndDeletedFalse(repoId);
    }

    @Override
    public Offer getById(Integer id){
        return offerRepository.findByIdAndDeletedFalse(id);
    }

    @Override
    public Offer save(Offer offer){
        return offerRepository.save(offer);
    }

    @Override
    public List<Offer> getAllByLanguage(){
        return offerRepository.findAllByActiveTrueAndDeletedFalse();
    }
    @Override
    public Offer complete(Integer offerId){
        Offer offer = getById(offerId);
        if (offer.getDocumentRepoId()==null) {
            DocumentRepo documentRepo = documentRepoService.create(DocumentRepoType.Offer, offer.getId());
            offer.setDocumentRepoId(documentRepo.getId());
        }

        return offerRepository.save(offer);
    }

    public Integer getOfferFileIdByLanguage(Offer offer,String locale){
        switch (locale){
            case "uz": return offer.getFileUzId();
            case "oz": return offer.getFileOzId();
            case "ru": return offer.getFileRuId();
            default: return offer.getFileRuId();
        }
    }

}