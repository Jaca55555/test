package uz.maroqand.ecology.core.service.sys;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.maroqand.ecology.core.entity.Didox;
import uz.maroqand.ecology.core.entity.sys.SendingData;

public interface DidoxService {
    Page<Didox> getAjaxList(Pageable pageable);

    void save(Didox didox);
}
