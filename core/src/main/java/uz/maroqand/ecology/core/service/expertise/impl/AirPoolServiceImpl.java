package uz.maroqand.ecology.core.service.expertise.impl;

import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.entity.expertise.AirPool;
import uz.maroqand.ecology.core.repository.expertise.AirPoolRepository;
import uz.maroqand.ecology.core.service.expertise.AirPoolService;

@Service
public class AirPoolServiceImpl implements AirPoolService {

    private final AirPoolRepository  airPoolRepository;

    public AirPoolServiceImpl(AirPoolRepository airPoolRepository) {
        this.airPoolRepository = airPoolRepository;
    }

    @Override
    public AirPool save(AirPool airPool) {
        return airPoolRepository.save(airPool);
    }

    @Override
    public AirPool getById(Integer id) {
        return airPoolRepository.findByIdAndDeletedFalse(id);
    }
}
