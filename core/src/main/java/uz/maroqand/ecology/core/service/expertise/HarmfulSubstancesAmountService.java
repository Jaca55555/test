package uz.maroqand.ecology.core.service.expertise;


import uz.maroqand.ecology.core.entity.expertise.HarmfulSubstancesAmount;

public interface HarmfulSubstancesAmountService {

    HarmfulSubstancesAmount save(HarmfulSubstancesAmount harmfulSubstancesAmount);

    HarmfulSubstancesAmount getById(Integer id);

}
