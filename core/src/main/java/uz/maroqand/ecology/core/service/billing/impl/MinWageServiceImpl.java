package uz.maroqand.ecology.core.service.billing.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.repository.billing.MinWageRepository;
import uz.maroqand.ecology.core.service.billing.MinWageService;

/**
 * Created by Utkirbek Boltaev on 15.06.2019.
 * (uz)
 * (ru)
 */

@Service
public class MinWageServiceImpl implements MinWageService {

    private final MinWageRepository minWageRepository;

    @Autowired
    public MinWageServiceImpl(MinWageRepository minWageRepository) {
        this.minWageRepository = minWageRepository;
    }

}