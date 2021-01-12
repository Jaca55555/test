package uz.maroqand.ecology.core.service.expertise;


import uz.maroqand.ecology.core.entity.expertise.PollutionMeasures;

public interface PollutionMeasuresService {

    PollutionMeasures save(PollutionMeasures pollutionMeasures);

    PollutionMeasures getById(Integer id);

}
