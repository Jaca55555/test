package uz.maroqand.ecology.core.service.sys.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.entity.sys.Soato;
import uz.maroqand.ecology.core.repository.sys.SoatoRepository;
import uz.maroqand.ecology.core.service.sys.SoatoService;

import java.util.Arrays;
import java.util.List;

@Service
public class SoatoServiceImpl implements SoatoService {

    @Autowired
    private SoatoRepository soatoRepository;

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
        return soatoRepository.findByLevelOrderByNameAsc(1);
    }

    @Cacheable("SoatoServiceImpl.getSubRegions")
    public List<Soato> getSubRegions() {
        return soatoRepository.findByLevelOrderByNameAsc(2);
    }

}
