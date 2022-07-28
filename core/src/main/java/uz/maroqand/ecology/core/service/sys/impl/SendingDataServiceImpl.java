package uz.maroqand.ecology.core.service.sys.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.entity.sys.SendingData;
import uz.maroqand.ecology.core.repository.sys.SendingDataREpository;
import uz.maroqand.ecology.core.service.sys.SendingDataService;

@Service
public class SendingDataServiceImpl implements SendingDataService {

    private final SendingDataREpository sendingDataREpository;

    public SendingDataServiceImpl(SendingDataREpository sendingDataREpository) {
        this.sendingDataREpository = sendingDataREpository;
    }

    @Override
    public Page<SendingData> getAjaxList(Pageable pageable) {
        return sendingDataREpository.findAll(pageable);
    }

    @Override
    public void save(SendingData sendingData) {
        sendingDataREpository.save(sendingData);
    }
}
