package uz.maroqand.ecology.core.service.sys.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.entity.sys.Soato;
import uz.maroqand.ecology.core.repository.sys.SoatoRepository;
import uz.maroqand.ecology.core.service.sys.SoatoService;

import java.util.Arrays;
import java.util.List;

@Service
public class SoatoServiceImpl implements SoatoService {

    private SoatoRepository soatoRepository;

    @Autowired
    public SoatoServiceImpl(SoatoRepository soatoRepository) {
        this.soatoRepository = soatoRepository;
    }

    @Override
    public Soato getById(Integer id) {
        System.out.println("soatId == " + id);
        Soato soato = soatoRepository.getOne(id);
        System.out.println(soato.getName() + " " + soato.getNameRu() + " " +soato.getNameOz());
        return soato;
    }

    @Cacheable("SoatoServiceImpl.getRegions")
    public List<Soato> getRegions() {
        return soatoRepository.getAll();
    }

    @Cacheable("SoatoServiceImpl.getSubRegions")
    public List<Soato> getSubRegions() {
        return soatoRepository.findByLevelOrderByNameAsc(2);
    }
    public List<Soato> getSubregionsbyregionId(Long id){
        return soatoRepository.findByParentId(id);
    }
    public Page<Soato> getFiltered(Pageable pageable){
        return soatoRepository.findAll(pageable);
    }
}
