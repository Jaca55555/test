package uz.maroqand.ecology.core.service.expertise.impl;

import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.entity.expertise.BoilerCharacteristics;
import uz.maroqand.ecology.core.entity.expertise.BoilerCharacteristicsEnum;
import uz.maroqand.ecology.core.entity.expertise.HarmfulSubstancesAmount;
import uz.maroqand.ecology.core.entity.expertise.RegApplicationCategoryFourAdditional;
import uz.maroqand.ecology.core.repository.expertise.RegApplicationCategoryFourAdditionalRepository;
import uz.maroqand.ecology.core.service.expertise.BoilerCharacteristicsService;
import uz.maroqand.ecology.core.service.expertise.RegApplicationCategoryFourAdditionalService;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Service
public class RegApplicationCategoryFourAdditionalServiceImpl implements RegApplicationCategoryFourAdditionalService {

    private final RegApplicationCategoryFourAdditionalRepository categoryFourAdditionalRepository;
    private final BoilerCharacteristicsService boilerCharacteristicsService;

    public RegApplicationCategoryFourAdditionalServiceImpl(RegApplicationCategoryFourAdditionalRepository categoryFourAdditionalRepository, BoilerCharacteristicsService boilerCharacteristicsService) {
        this.categoryFourAdditionalRepository = categoryFourAdditionalRepository;
        this.boilerCharacteristicsService = boilerCharacteristicsService;
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

    @Override
    public void createBolier(RegApplicationCategoryFourAdditional regApplicationCategoryFourAdditional,Integer userId) {
        Set<BoilerCharacteristics> boilerCharacteristicsSet = new HashSet<>();
        for (int index=0;index<BoilerCharacteristicsEnum.getBoilerCharacteristics().size(); index++){
            BoilerCharacteristics characteristics = new BoilerCharacteristics();
            characteristics.setName(BoilerCharacteristicsEnum.getBoilerCharacteristicById(index).getName());
            characteristics.setType(BoilerCharacteristicsEnum.getBoilerCharacteristicById(index).getType()
            );
            characteristics.setAmount(0.0);
            characteristics.setDeleted(Boolean.FALSE);
            boilerCharacteristicsService.save(characteristics);
            boilerCharacteristicsSet.add(characteristics);
        }
        regApplicationCategoryFourAdditional.setBoilerCharacteristics(boilerCharacteristicsSet);
        update(regApplicationCategoryFourAdditional,userId);
    }

    @Override
    public HarmfulSubstancesAmount step4_3_total(RegApplicationCategoryFourAdditional regApplicationCategoryFourAdditional) {
        HarmfulSubstancesAmount harmfulSubstancesAmountTotal = new HarmfulSubstancesAmount();
        harmfulSubstancesAmountTotal.setName("sys_total");
        harmfulSubstancesAmountTotal.setSubstancesAmount(0.0);
        harmfulSubstancesAmountTotal.setForCleaning(0.0);
        harmfulSubstancesAmountTotal.setCaught(0.0);
        harmfulSubstancesAmountTotal.setUsed(0.0);
        harmfulSubstancesAmountTotal.setAtmosphereAmount(0.0);
        Set<HarmfulSubstancesAmount> harmfulSubstancesAmountSet = regApplicationCategoryFourAdditional.getHarmfulSubstancesAmounts();

        for (HarmfulSubstancesAmount harmfulSubstancesAmount:harmfulSubstancesAmountSet) {
            harmfulSubstancesAmountTotal.setSubstancesAmount(harmfulSubstancesAmount.getSubstancesAmount() + harmfulSubstancesAmountTotal.getSubstancesAmount());
            harmfulSubstancesAmountTotal.setForCleaning(harmfulSubstancesAmount.getForCleaning() + harmfulSubstancesAmountTotal.getForCleaning());
            harmfulSubstancesAmountTotal.setCaught(harmfulSubstancesAmount.getCaught() + harmfulSubstancesAmountTotal.getCaught());
            harmfulSubstancesAmountTotal.setUsed(harmfulSubstancesAmount.getUsed() + harmfulSubstancesAmountTotal.getUsed());
            harmfulSubstancesAmountTotal.setAtmosphereAmount(harmfulSubstancesAmount.getAtmosphereAmount() + harmfulSubstancesAmountTotal.getAtmosphereAmount());
        }

        return harmfulSubstancesAmountTotal;
    }

    @Override
    public RegApplicationCategoryFourAdditional update(RegApplicationCategoryFourAdditional regApplicationCategoryFourAdditional, Integer userId) {
        regApplicationCategoryFourAdditional.setUpdateAt(new Date());
        regApplicationCategoryFourAdditional.setUpdateById(userId);
        return save(regApplicationCategoryFourAdditional);
    }

    @Override
    public RegApplicationCategoryFourAdditional saveStep3(RegApplicationCategoryFourAdditional regApplicationCategoryFourAdditional, RegApplicationCategoryFourAdditional regApplicationCategoryFourAdditionalOld, Integer userId) {
        regApplicationCategoryFourAdditionalOld.setCommonArea(regApplicationCategoryFourAdditional.getCommonArea());
        regApplicationCategoryFourAdditionalOld.setBuildingUnderArea(regApplicationCategoryFourAdditional.getBuildingUnderArea());
        regApplicationCategoryFourAdditionalOld.setAreaAdditional(regApplicationCategoryFourAdditional.getAreaAdditional());
        regApplicationCategoryFourAdditionalOld.setRoadArea(regApplicationCategoryFourAdditional.getRoadArea());
        regApplicationCategoryFourAdditionalOld.setWorkSchedule(regApplicationCategoryFourAdditional.getWorkSchedule());
        regApplicationCategoryFourAdditionalOld.setProductionCapacity(regApplicationCategoryFourAdditional.getProductionCapacity());
        regApplicationCategoryFourAdditionalOld.setTechnologicalProcess(regApplicationCategoryFourAdditional.getTechnologicalProcess());
        regApplicationCategoryFourAdditionalOld.setMaterialAdditional(regApplicationCategoryFourAdditional.getMaterialAdditional());
        regApplicationCategoryFourAdditionalOld.setSourceMaterial(regApplicationCategoryFourAdditional.getSourceMaterial());
        return update(regApplicationCategoryFourAdditionalOld,userId);
    }

    @Override
    public RegApplicationCategoryFourAdditional saveStep4_3(RegApplicationCategoryFourAdditional regApplicationCategoryFourAdditional, RegApplicationCategoryFourAdditional regApplicationCategoryFourAdditionalOld, Integer userId) {
        regApplicationCategoryFourAdditionalOld.setHardValueOne(regApplicationCategoryFourAdditional.getHardValueOne());
        regApplicationCategoryFourAdditionalOld.setHardValueTwo(regApplicationCategoryFourAdditional.getHardValueTwo());
        regApplicationCategoryFourAdditionalOld.setHardValueThree(regApplicationCategoryFourAdditional.getHardValueThree());
        regApplicationCategoryFourAdditionalOld.setHardValueFour(regApplicationCategoryFourAdditional.getHardValueFour());
        regApplicationCategoryFourAdditionalOld.setHardValueFive(regApplicationCategoryFourAdditional.getHardValueFive());

        regApplicationCategoryFourAdditionalOld.setLiquidValueOne(regApplicationCategoryFourAdditional.getLiquidValueOne());
        regApplicationCategoryFourAdditionalOld.setLiquidValueTwo(regApplicationCategoryFourAdditional.getLiquidValueTwo());
        regApplicationCategoryFourAdditionalOld.setLiquidValueThree(regApplicationCategoryFourAdditional.getLiquidValueThree());
        regApplicationCategoryFourAdditionalOld.setLiquidValueFour(regApplicationCategoryFourAdditional.getLiquidValueFour());
        regApplicationCategoryFourAdditionalOld.setLiquidValueFive(regApplicationCategoryFourAdditional.getLiquidValueFive());
        return update(regApplicationCategoryFourAdditionalOld,userId);
    }

    @Override
    public RegApplicationCategoryFourAdditional saveStep5(RegApplicationCategoryFourAdditional regApplicationCategoryFourAdditional, RegApplicationCategoryFourAdditional regApplicationCategoryFourAdditionalOld, Integer userId) {

        regApplicationCategoryFourAdditionalOld.setAboutWaste(regApplicationCategoryFourAdditional.getAboutWaste());
        regApplicationCategoryFourAdditionalOld.setWaterVolume(regApplicationCategoryFourAdditional.getWaterVolume());
        regApplicationCategoryFourAdditionalOld.setSourceWater(regApplicationCategoryFourAdditional.getSourceWater());
        regApplicationCategoryFourAdditionalOld.setHeatingSystem(regApplicationCategoryFourAdditional.getHeatingSystem());
        regApplicationCategoryFourAdditionalOld.setFirefightingMeasures(regApplicationCategoryFourAdditional.getFirefightingMeasures());
        regApplicationCategoryFourAdditionalOld.setVentilation(regApplicationCategoryFourAdditional.getVentilation());
        regApplicationCategoryFourAdditionalOld.setEmergencyMeasures(regApplicationCategoryFourAdditional.getEmergencyMeasures());

        return update(regApplicationCategoryFourAdditionalOld,userId);
    }

    @Override
    public RegApplicationCategoryFourAdditional saveStep7(RegApplicationCategoryFourAdditional regApplicationCategoryFourAdditional, RegApplicationCategoryFourAdditional regApplicationCategoryFourAdditionalOld, Integer userId) {
        regApplicationCategoryFourAdditionalOld.setObjectPlacementPlan(regApplicationCategoryFourAdditional.getObjectPlacementPlan());
        regApplicationCategoryFourAdditionalOld.setProductionDescription(regApplicationCategoryFourAdditional.getProductionDescription());
        regApplicationCategoryFourAdditionalOld.setSewageAvailability(regApplicationCategoryFourAdditional.getSewageAvailability());
        regApplicationCategoryFourAdditionalOld.setDiscardAmountAbout(regApplicationCategoryFourAdditional.getDiscardAmountAbout());
        regApplicationCategoryFourAdditionalOld.setWasteAmountAbout(regApplicationCategoryFourAdditional.getWasteAmountAbout());
        regApplicationCategoryFourAdditionalOld.setEvents(regApplicationCategoryFourAdditional.getEvents());
        return update(regApplicationCategoryFourAdditionalOld,userId);
    }
}
