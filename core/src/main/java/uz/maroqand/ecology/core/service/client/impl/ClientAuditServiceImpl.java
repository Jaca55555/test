package uz.maroqand.ecology.core.service.client.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.repository.client.ClientAuditRepository;
import uz.maroqand.ecology.core.service.client.ClientAuditService;

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

}