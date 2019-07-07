package uz.maroqand.ecology.core.service.client.impl;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.entity.client.Client;
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
    private final Gson gson;

    @Autowired
    public ClientAuditServiceImpl(ClientAuditRepository clientAuditRepository, Gson gson) {
        this.clientAuditRepository = clientAuditRepository;
        this.gson = gson;
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
        clientAudit.setBeforeChanges(before);
        clientAudit.setAfterChanges(after);

        clientAudit.setMessage(message);
        clientAudit.setUserId(userId);
        clientAudit.setUserAdditionalId(userAdditionalId);
        clientAudit.setRegisteredDate(new Date());

        return clientAuditRepository.save(clientAudit);
    }

    @Override
    public List<ClientAudit> getByClientId(Integer clientId){
        List<ClientAudit> clientAuditList = clientAuditRepository.findByClientIdOrderByIdDesc(clientId);
        for (ClientAudit clientAudit:clientAuditList){
            clientAudit.setBefore(gson.fromJson(clientAudit.getBeforeChanges(), Client.class));
            clientAudit.setAfter(gson.fromJson(clientAudit.getAfterChanges(), Client.class));

            if(clientAudit.getBefore()==null){
                clientAudit.setBefore(new Client());
            }
            if(clientAudit.getAfter()==null){
                clientAudit.setAfter(new Client());
            }
        }
        return clientAuditList;
    }

}