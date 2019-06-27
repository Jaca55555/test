package uz.maroqand.ecology.core.service.client.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.entity.client.ClientAudit;
import uz.maroqand.ecology.core.repository.client.ClientAuditRepository;
import uz.maroqand.ecology.core.service.client.ClientAuditService;

import java.util.Date;
import java.util.List;

/**
 * Created by Utkirbek Boltaev on 15.06.2019.
 * (uz)
 * (ru)
 */

@Service
public class ClientAuditServiceImpl implements ClientAuditService {

    private final ClientAuditRepository clientAuditRepository;

    @Autowired
    public ClientAuditServiceImpl(ClientAuditRepository clientAuditRepository) {
        this.clientAuditRepository = clientAuditRepository;
    }

    @Override
    public ClientAudit create(
            Integer clientId,
            String before,
            String after,
            String message,
            Integer userId,
            Integer userAdditionalId
    ){
        ClientAudit clientAudit = new ClientAudit();
        clientAudit.setClientId(clientId);
        clientAudit.setChangesSerialized(before+","+after);

        clientAudit.setMessage(message);
        clientAudit.setUserId(userId);
        clientAudit.setUserAdditionalId(userAdditionalId);
        clientAudit.setRegisteredDate(new Date());

        return clientAuditRepository.save(clientAudit);
    }

    @Override
    public List<ClientAudit> getByClientId(Integer clientId){
        return clientAuditRepository.findByClientIdOrderByIdDesc(clientId);
    }

}