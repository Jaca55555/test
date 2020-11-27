package uz.maroqand.ecology.core.service.expertise;


import uz.maroqand.ecology.core.entity.expertise.RegApplicationCategoryFourAdditional;


public interface RegApplicationCategoryFourAdditionalService {

    RegApplicationCategoryFourAdditional getById(Integer id);

    RegApplicationCategoryFourAdditional getByRegApplicationId(Integer id);

    RegApplicationCategoryFourAdditional save(RegApplicationCategoryFourAdditional regApplicationCategoryFourAdditional);

}
