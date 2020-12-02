package uz.maroqand.ecology.core.service.expertise.impl;

import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.entity.expertise.DescriptionOfSources;
import uz.maroqand.ecology.core.repository.expertise.DescriptionOfSourcesRepository;
import uz.maroqand.ecology.core.service.expertise.DescriptionOfSourcesService;

@Service
public class DescriptionOfSourcesServiceImpl implements DescriptionOfSourcesService {

    private final DescriptionOfSourcesRepository descriptionOfSourcesRepository;

    public DescriptionOfSourcesServiceImpl(DescriptionOfSourcesRepository descriptionOfSourcesRepository) {
        this.descriptionOfSourcesRepository = descriptionOfSourcesRepository;
    }

    @Override
    public DescriptionOfSources save(DescriptionOfSources descriptionOfSources) {
        return descriptionOfSourcesRepository.save(descriptionOfSources);
    }

    @Override
    public DescriptionOfSources getById(Integer id) {
        return descriptionOfSourcesRepository.findByIdAndDeletedFalse(id);
    }
}
