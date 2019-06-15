package uz.maroqand.ecology.core.service.expertise.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.entity.expertise.Offer;
import uz.maroqand.ecology.core.repository.expertise.OfferRepository;
import uz.maroqand.ecology.core.service.expertise.OfferService;

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
    public Offer getOffer(String language){
        return offerRepository.findTop1ByActiveTrueAndLanguageOrderByIdDesc(language);
    }

}