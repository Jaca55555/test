package uz.maroqand.ecology.core.service.expertise.impl;

import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.entity.expertise.DescriptionOfSourcesAdditional;
import uz.maroqand.ecology.core.repository.expertise.DescriptionOfSourcesAdditionalRepository;
import uz.maroqand.ecology.core.service.expertise.DescriptionOfSourcesAdditionalService;

@Service
public class DescriptionOfSourcesAdditionalServiceImpl implements DescriptionOfSourcesAdditionalService {

    private final DescriptionOfSourcesAdditionalRepository descriptionOfSourcesAdditionalRepository;

    public DescriptionOfSourcesAdditionalServiceImpl(DescriptionOfSourcesAdditionalRepository descriptionOfSourcesAdditionalRepository) {
        this.descriptionOfSourcesAdditionalRepository = descriptionOfSourcesAdditionalRepository;
    }

    @Override
    public DescriptionOfSourcesAdditional save(DescriptionOfSourcesAdditional descriptionOfSourcesAdditional) {
        return descriptionOfSourcesAdditionalRepository.save(descriptionOfSourcesAdditional);
    }

    @Override
    public DescriptionOfSourcesAdditional getById(Integer id) {
        return descriptionOfSourcesAdditionalRepository.findByIdAndDeletedFalse(id);
    }
}
