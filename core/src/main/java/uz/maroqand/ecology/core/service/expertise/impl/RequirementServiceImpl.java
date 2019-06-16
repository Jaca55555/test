package uz.maroqand.ecology.core.service.expertise.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.constant.expertise.Category;
import uz.maroqand.ecology.core.entity.expertise.Requirement;
import uz.maroqand.ecology.core.repository.expertise.RequirementRepository;
import uz.maroqand.ecology.core.service.expertise.RequirementService;

import java.util.List;

/**
 * Created by Utkirbek Boltaev on 15.06.2019.
 * (uz)
 * (ru)
 */

@Service
public class RequirementServiceImpl implements RequirementService {

    private final RequirementRepository requirementRepository;

    @Autowired
    public RequirementServiceImpl(RequirementRepository requirementRepository) {
        this.requirementRepository = requirementRepository;
    }

    public Requirement getById(Integer id){
        return requirementRepository.getOne(id);
    }

    public List<Requirement> getRequirementMaterials(Integer objectExpertiseId, Category category){
        return requirementRepository.findByObjectExpertiseIdAndCategory(objectExpertiseId,category);
    }

}