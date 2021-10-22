package uz.maroqand.ecology.core.repository.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.core.entity.client.Client;

import java.util.List;

/**
 * Created by Utkirbek Boltaev on 14.06.2019.
 * (uz)
 * (ru)
 */
@Repository
public interface ClientRepository extends JpaRepository<Client, Integer>, JpaSpecificationExecutor<Client> {

    Client findTop1ByTinAndDeletedFalseOrderByIdDesc(Integer tin);

    List<Client> findByTinAndDeletedFalse(Integer tin);
    List<Client> findByTinAndPinflAndDeletedFalse(Integer tin,String pinfl);
    Client findByIdAndDeletedFalse(Integer Id);

    Client findTop1ByPinflAndDeletedFalseOrderByIdDesc(String pinfl);

    Client findTop1ByPassportNumberAndDeletedFalseOrderByIdDesc(String passportNumber);

}
