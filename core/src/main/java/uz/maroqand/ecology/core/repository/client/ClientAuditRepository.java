package uz.maroqand.ecology.core.repository.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.core.entity.client.ClientAudit;

import java.util.List;

/**
 * Created by Utkirbek Boltaev on 15.06.2019.
 * (uz)
 * (ru)
 */
@Repository
public interface ClientAuditRepository extends JpaRepository<ClientAudit, Integer> {

    List<ClientAudit> findByClientIdOrderByIdDesc(Integer clientId);

}
