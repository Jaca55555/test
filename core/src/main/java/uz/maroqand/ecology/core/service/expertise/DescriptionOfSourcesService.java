package uz.maroqand.ecology.core.service.expertise;


import uz.maroqand.ecology.core.entity.expertise.DescriptionOfSources;

public interface DescriptionOfSourcesService {

    DescriptionOfSources save(DescriptionOfSources descriptionOfSources);

    DescriptionOfSources getById(Integer id);

}
