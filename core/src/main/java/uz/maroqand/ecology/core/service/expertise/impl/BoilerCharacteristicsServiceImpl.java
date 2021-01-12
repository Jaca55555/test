package uz.maroqand.ecology.core.service.expertise.impl;

import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.entity.expertise.BoilerCharacteristics;
import uz.maroqand.ecology.core.repository.expertise.BoilerCharacteristicsRepository;
import uz.maroqand.ecology.core.service.expertise.BoilerCharacteristicsService;

@Service
public class BoilerCharacteristicsServiceImpl implements BoilerCharacteristicsService {

    private final BoilerCharacteristicsRepository boilerCharacteristicsRepository;

    public BoilerCharacteristicsServiceImpl(BoilerCharacteristicsRepository boilerCharacteristicsRepository) {
        this.boilerCharacteristicsRepository = boilerCharacteristicsRepository;
    }


    @Override
    public BoilerCharacteristics save(BoilerCharacteristics boilerCharacteristics) {
        return boilerCharacteristicsRepository.save(boilerCharacteristics);
    }

    @Override
    public BoilerCharacteristics getById(Integer id) {
        return boilerCharacteristicsRepository.findByIdAndDeletedFalse(id);
    }
}
