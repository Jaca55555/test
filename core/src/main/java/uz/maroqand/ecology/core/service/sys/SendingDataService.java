package uz.maroqand.ecology.core.service.sys;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.maroqand.ecology.core.entity.sys.SendingData;

public interface SendingDataService {
    Page<SendingData> getAjaxList(Pageable pageable);
}
