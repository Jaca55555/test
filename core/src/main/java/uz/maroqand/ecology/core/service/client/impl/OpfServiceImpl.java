package uz.maroqand.ecology.core.service.client.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.entity.client.Opf;
import uz.maroqand.ecology.core.repository.client.OpfRepository;
import uz.maroqand.ecology.core.service.client.OpfService;

import java.util.List;

@Service
public class OpfServiceImpl implements OpfService {

    private OpfRepository opfRepository;

    @Autowired
    public OpfServiceImpl(OpfRepository opfRepository) {
        this.opfRepository = opfRepository;
    }

    @Cacheable("getOpfList")
    public List<Opf> getOpfList() {
        return opfRepository.findByOrderByIdAsc();
    }

    @Cacheable("getOpfById")
    public Opf getById(Integer id){
        return opfRepository.getOne(id);
    }

}
