package uz.maroqand.ecology.core.service.expertise.impl;

import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.entity.expertise.HarmfulSubstancesAmount;
import uz.maroqand.ecology.core.repository.expertise.HarmfulSubstancesAmountRepository;
import uz.maroqand.ecology.core.service.expertise.HarmfulSubstancesAmountService;

@Service
public class HarmfulSubstancesAmountServiceImpl implements HarmfulSubstancesAmountService {

    private final HarmfulSubstancesAmountRepository harmfulSubstancesAmountRepository;

    public HarmfulSubstancesAmountServiceImpl(HarmfulSubstancesAmountRepository harmfulSubstancesAmountRepository) {
        this.harmfulSubstancesAmountRepository = harmfulSubstancesAmountRepository;
    }

    @Override
    public HarmfulSubstancesAmount save(HarmfulSubstancesAmount harmfulSubstancesAmount) {
        return harmfulSubstancesAmountRepository.save(harmfulSubstancesAmount);
    }

    @Override
    public HarmfulSubstancesAmount getById(Integer id) {
        return harmfulSubstancesAmountRepository.findByIdAndDeletedFalse(id);
    }
}
