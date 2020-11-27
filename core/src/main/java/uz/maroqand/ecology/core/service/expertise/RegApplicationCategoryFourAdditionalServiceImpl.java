package uz.maroqand.ecology.core.service.expertise;

import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.entity.expertise.RegApplicationCategoryFourAdditional;
import uz.maroqand.ecology.core.repository.expertise.RegApplicationCategoryFourAdditionalRepository;

@Service
public class RegApplicationCategoryFourAdditionalServiceImpl implements RegApplicationCategoryFourAdditionalService {

    private final RegApplicationCategoryFourAdditionalRepository categoryFourAdditionalRepository;

    public RegApplicationCategoryFourAdditionalServiceImpl(RegApplicationCategoryFourAdditionalRepository categoryFourAdditionalRepository) {
        this.categoryFourAdditionalRepository = categoryFourAdditionalRepository;
    }

    @Override
    public RegApplicationCategoryFourAdditional getById(Integer id) {
        return categoryFourAdditionalRepository.findByIdAndDeletedFalse(id);
    }

    @Override
    public RegApplicationCategoryFourAdditional getByRegApplicationId(Integer id) {
        return categoryFourAdditionalRepository.findByRegApplicationIdAndDeletedFalse(id);
    }

    @Override
    public RegApplicationCategoryFourAdditional save(RegApplicationCategoryFourAdditional regApplicationCategoryFourAdditional) {
        return categoryFourAdditionalRepository.save(regApplicationCategoryFourAdditional);
    }
}
