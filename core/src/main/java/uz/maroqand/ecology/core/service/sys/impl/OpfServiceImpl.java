package uz.maroqand.ecology.core.service.sys.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.entity.sys.Opf;
import uz.maroqand.ecology.core.repository.sys.OpfRepository;
import uz.maroqand.ecology.core.service.sys.OpfService;

import java.util.List;

@Service
public class OpfServiceImpl implements OpfService {

    @Autowired
    private OpfRepository opfRepository;

    public OpfServiceImpl(OpfRepository opfRepository) {
        this.opfRepository = opfRepository;
    }

    @Cacheable("getOpfList")
    public List<Opf> getOpfList() {
        return opfRepository.findByOrderByIdAsc();
    }


}
