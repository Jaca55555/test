package uz.maroqand.ecology.core.service.sys.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.entity.sys.Option;
import uz.maroqand.ecology.core.repository.sys.OptionRepository;
import uz.maroqand.ecology.core.service.sys.OptionService;

/**
 * Created by Utkirbek Boltaev on 03.06.2019.
 * (uz)
 * (ru)
 */
@Service
public class OptionServiceImpl implements OptionService {

    private final OptionRepository optionRepository;

    @Autowired
    public OptionServiceImpl(OptionRepository optionRepository) {
        this.optionRepository = optionRepository;
    }

    public Option getOption(String name) {
        return optionRepository.findByNameOrderByIdDesc(name);
    }

}