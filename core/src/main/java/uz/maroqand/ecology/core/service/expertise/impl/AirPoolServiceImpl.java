package uz.maroqand.ecology.core.service.expertise.impl;

import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.entity.expertise.AirPool;
import uz.maroqand.ecology.core.entity.expertise.DescriptionOfSources;
import uz.maroqand.ecology.core.repository.expertise.AirPoolRepository;
import uz.maroqand.ecology.core.service.expertise.AirPoolService;
import uz.maroqand.ecology.core.service.expertise.DescriptionOfSourcesService;

import java.util.Set;

@Service
public class AirPoolServiceImpl implements AirPoolService {

    private final AirPoolRepository  airPoolRepository;
    private final DescriptionOfSourcesService descriptionOfSourcesService;

    public AirPoolServiceImpl(AirPoolRepository airPoolRepository, DescriptionOfSourcesService descriptionOfSourcesService) {
        this.airPoolRepository = airPoolRepository;
        this.descriptionOfSourcesService = descriptionOfSourcesService;
    }

    @Override
    public AirPool save(AirPool airPool) {
        return airPoolRepository.save(airPool);
    }

    @Override
    public AirPool removeAllDescriptionNotSaved(AirPool airPool) {
        if (airPool.getDescriptionOfSources()!=null && !airPool.getDescriptionOfSources().isEmpty()){
            Set<DescriptionOfSources> descriptionOfSourcesSet = airPool.getDescriptionOfSources();
            for (DescriptionOfSources descriptionOfSources:descriptionOfSourcesSet){
                descriptionOfSources.setDeleted(Boolean.TRUE);
                descriptionOfSourcesService.save(descriptionOfSources);
            }
            airPool.setDescriptionOfSources(null);
        }
        return airPool;
    }

    @Override
    public AirPool getById(Integer id) {
        return airPoolRepository.findByIdAndDeletedFalse(id);
    }
}
