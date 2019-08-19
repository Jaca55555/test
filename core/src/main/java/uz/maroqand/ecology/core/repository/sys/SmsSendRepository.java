package uz.maroqand.ecology.core.repository.sys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.core.entity.sys.SmsSend;

/**
 * Created by Utkirbek Boltaev on 10.06.2019.
 * (uz)
 * (ru)
 */
@Repository
public interface SmsSendRepository extends JpaRepository<SmsSend, Integer>, JpaSpecificationExecutor<SmsSend> {

    SmsSend findTop1ByRegApplicationIdOrderByIdDesc(Integer id);

}