package uz.maroqand.ecology.core.service.sys.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.entity.sys.GNKSoato;
import uz.maroqand.ecology.core.repository.sys.GNKSoatoRepository;
import uz.maroqand.ecology.core.service.sys.GNKSoatoService;

/**
 * Created by Utkirbek Boltaev on 07.08.2019.
 * (uz)
 * (ru)
 */

@Service
public class GNKSoatoServiceImpl implements GNKSoatoService {

    private final GNKSoatoRepository gNKSoatoRepository;

    @Autowired
    public GNKSoatoServiceImpl(GNKSoatoRepository gNKSoatoRepository) {
        this.gNKSoatoRepository = gNKSoatoRepository;
    }

    public GNKSoato getSoato(Integer gnkSoatoId){
        return gNKSoatoRepository.findByGnkSoatoId(gnkSoatoId);
    }

}