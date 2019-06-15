package uz.maroqand.ecology.core.service.expertise.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.entity.expertise.ObjectExpertise;
import uz.maroqand.ecology.core.repository.expertise.ObjectExpertiseRepository;
import uz.maroqand.ecology.core.service.expertise.ObjectExpertiseService;

import java.util.List;

@Service
public class ObjectExpertiseServiceImpl implements ObjectExpertiseService {

    private final ObjectExpertiseRepository objectExpertiseRepository;

    @Autowired
    public ObjectExpertiseServiceImpl(ObjectExpertiseRepository objectExpertiseRepository) {
        this.objectExpertiseRepository = objectExpertiseRepository;
    }

    @Override
    public List<ObjectExpertise> getList() {
        return objectExpertiseRepository.findAll();
    }
}
