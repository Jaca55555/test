package uz.maroqand.ecology.core.repository.expertise;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.maroqand.ecology.core.entity.expertise.PerformerHistory;

import java.util.List;

public interface PerformerHistoryRepository  extends JpaRepository<PerformerHistory, Integer> {

    List<PerformerHistory> findByApplicationNumberAndDeletedFalse(Integer applicationNumber);
}
