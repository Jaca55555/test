package uz.maroqand.ecology.core.service.expertise.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.entity.expertise.Offer;
import uz.maroqand.ecology.core.repository.expertise.OfferRepository;
import uz.maroqand.ecology.core.service.expertise.OfferService;

import java.util.List;

/**
 * Created by Utkirbek Boltaev on 15.06.2019.
 * (uz)
 * (ru)
 */
@Service
public class OfferServiceImpl implements OfferService {

    private final OfferRepository offerRepository;

    @Autowired
    public OfferServiceImpl(OfferRepository offerRepository) {
        this.offerRepository = offerRepository;
    }

    @Override
    public Offer getOffer(){
        return offerRepository.findTop1ByActiveTrueOrderByIdDesc();
    }

    @Override
    public Page<Offer> getAll(Pageable pageable){
        return offerRepository.findAllByActiveTrueAndDeletedFalse(pageable);
    }

    @Override
    public Offer getById(Integer id){
        return offerRepository.findById(id).get();
    }

    @Override
    public Offer save(Offer offer){
        return offerRepository.save(offer);
    }

    @Override
    public List<Offer> getAllByLanguage(){
        return offerRepository.findAllByActiveTrueAndDeletedFalse();
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