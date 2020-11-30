package uz.maroqand.ecology.core.service.expertise.impl;

import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.entity.expertise.PollutionMeasures;
import uz.maroqand.ecology.core.repository.expertise.PollutionMeasuresRepository;
import uz.maroqand.ecology.core.service.expertise.PollutionMeasuresService;

@Service
public class PollutionMeasuresServiceImpl implements PollutionMeasuresService {

    private final PollutionMeasuresRepository pollutionMeasuresRepository;

    public PollutionMeasuresServiceImpl(PollutionMeasuresRepository pollutionMeasuresRepository) {
        this.pollutionMeasuresRepository = pollutionMeasuresRepository;
    }

    @Override
    public PollutionMeasures save(PollutionMeasures pollutionMeasures) {
        return pollutionMeasuresRepository.save(pollutionMeasures);
    }

    @Override
    public PollutionMeasures getById(Integer id) {
        return pollutionMeasuresRepository.findByIdAndDeletedFalse(id);
    }
}
