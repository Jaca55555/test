package uz.maroqand.ecology.core.service.expertise.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.entity.expertise.Material;
import uz.maroqand.ecology.core.repository.expertise.MaterialRepository;
import uz.maroqand.ecology.core.service.expertise.MaterialService;

import java.util.List;

/**
 * Created by Utkirbek Boltaev on 15.06.2019.
 * (uz)
 * (ru)
 */

@Service
public class MaterialServiceImpl implements MaterialService {

    private final MaterialRepository materialRepository;

    @Autowired
    public MaterialServiceImpl(MaterialRepository materialRepository) {
        this.materialRepository = materialRepository;
    }

    @Override
    public Material getById(Integer id){
        return materialRepository.getOne(id);
    }

    @Override
    public Page<Material> getAll(Pageable pageable){
        return materialRepository.findAll(pageable);
    }

    @Override
    public Material save(Material material){
        return materialRepository.save(material);
    }

    @Override
    public List<Material> getList() {
        return materialRepository.findAll();
    }

}