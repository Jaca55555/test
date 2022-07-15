package uz.maroqand.ecology.core.service.expertise;


import uz.maroqand.ecology.core.entity.expertise.BoilerCharacteristics;

public interface BoilerCharacteristicsService {

    BoilerCharacteristics save(BoilerCharacteristics boilerCharacteristics);

    BoilerCharacteristics getById(Integer id);



}
