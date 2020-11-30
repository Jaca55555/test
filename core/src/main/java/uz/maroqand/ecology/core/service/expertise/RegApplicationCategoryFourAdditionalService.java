package uz.maroqand.ecology.core.service.expertise;


import uz.maroqand.ecology.core.entity.expertise.RegApplicationCategoryFourAdditional;


public interface RegApplicationCategoryFourAdditionalService {

    RegApplicationCategoryFourAdditional getById(Integer id);

    RegApplicationCategoryFourAdditional getByRegApplicationId(Integer id);

    RegApplicationCategoryFourAdditional save(RegApplicationCategoryFourAdditional regApplicationCategoryFourAdditional);

    void createBolier(RegApplicationCategoryFourAdditional regApplicationCategoryFourAdditional,Integer userId);

    RegApplicationCategoryFourAdditional update(RegApplicationCategoryFourAdditional regApplicationCategoryFourAdditional,Integer userId);

    RegApplicationCategoryFourAdditional saveStep3(RegApplicationCategoryFourAdditional regApplicationCategoryFourAdditional,RegApplicationCategoryFourAdditional regApplicationCategoryFourAdditionalOld, Integer userId);
    
    RegApplicationCategoryFourAdditional saveStep5(RegApplicationCategoryFourAdditional regApplicationCategoryFourAdditional,RegApplicationCategoryFourAdditional regApplicationCategoryFourAdditionalOld, Integer userId);

    RegApplicationCategoryFourAdditional saveStep7(RegApplicationCategoryFourAdditional regApplicationCategoryFourAdditional,RegApplicationCategoryFourAdditional regApplicationCategoryFourAdditionalOld, Integer userId);

}
