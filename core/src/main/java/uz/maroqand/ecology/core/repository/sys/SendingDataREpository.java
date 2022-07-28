package uz.maroqand.ecology.core.repository.sys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.core.entity.sys.SendingData;

@Repository
public interface SendingDataREpository extends JpaRepository<SendingData, Integer> {
}
