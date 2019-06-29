package uz.maroqand.ecology.core.service.client;

import uz.maroqand.ecology.core.entity.client.ClientAudit;

import java.util.List;

/**
 * Created by Utkirbek Boltaev on 15.06.2019.
 * (uz)
 * (ru)
 */
public interface ClientAuditService {

    ClientAudit create(
            Integer clientId,
            String before,
            String after,
            String message,
            Integer userId,
            Integer userAdditionalId
    );

    List<ClientAudit> getByClientId(Integer clientId);

}
