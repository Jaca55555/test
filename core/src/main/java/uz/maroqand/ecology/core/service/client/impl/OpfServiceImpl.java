package uz.maroqand.ecology.core.service.client.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.entity.client.Opf;
import uz.maroqand.ecology.core.repository.client.OpfRepository;
import uz.maroqand.ecology.core.service.client.OpfService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class OpfServiceImpl implements OpfService {

    private OpfRepository opfRepository;

    @Autowired
    public OpfServiceImpl(OpfRepository opfRepository) {
        this.opfRepository = opfRepository;
    }

    @Cacheable("getOpfLegalEntityList")
    public List<Opf> getOpfLegalEntityList() {
        Set<Integer> ids = new HashSet<>();
        ids.add(100);
        ids.add(200);
        ids.add(300);

        ids.add(1000);
        ids.add(2000);
        return opfRepository.findByIdInOrParentIdInOrderByIdAsc(ids,ids);
    }

    @Cacheable("getOpfIndividualList")
    public List<Opf> getOpfIndividualList() {
        Set<Integer> ids = new HashSet<>();
        ids.add(400);
        return opfRepository.findByIdInOrParentIdInOrderByIdAsc(ids,ids);
    }

    @Cacheable("getOpfList")
    public List<Opf> getOpfList() {
        return opfRepository.findByOrderByIdAsc();
    }

    @Cacheable("getOpfById")
    public Opf getById(Integer id){
        return opfRepository.getOne(id);
    }

    public Integer getByNameRu(String nameRu){
        Opf opf = opfRepository.findFirstByNameRu(nameRu.trim());
        return opf!=null? opf.getId():null;
    }

}
