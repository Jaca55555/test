package uz.maroqand.ecology.core.service.sys.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.entity.Didox;
import uz.maroqand.ecology.core.repository.DidoxRepository;
import uz.maroqand.ecology.core.service.sys.DidoxService;
@Service
public class DidoxServiceImpl implements DidoxService {

    private final DidoxRepository didoxRepository;

    public DidoxServiceImpl(DidoxRepository didoxRepository) {
        this.didoxRepository = didoxRepository;
    }

    @Override
    public Page<Didox> getAjaxList(Pageable pageable) {
        return didoxRepository.findAll(pageable);

    }

    @Override
    public void save(Didox didox) {
        didoxRepository.save(didox);

    }
}
