package uz.maroqand.ecology.core.repository.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.core.entity.client.Client;

/**
 * Created by Utkirbek Boltaev on 14.06.2019.
 * (uz)
 * (ru)
 */
@Repository
public interface ClientRepository extends JpaRepository<Client, Integer>, JpaSpecificationExecutor<Client> {

    Client findTop1ByTinAndDeletedFalseOrderByIdDesc(Integer tin);

    Client findTop1ByPinflAndDeletedFalseOrderByIdDesc(String pinfl);

    Client findTop1ByPassportSerialAndPassportNumberAndDeletedFalseOrderByIdDesc(String passportSerial, String passportNumber);

}
