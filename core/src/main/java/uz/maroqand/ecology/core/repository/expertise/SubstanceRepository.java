package uz.maroqand.ecology.core.repository.expertise;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import uz.maroqand.ecology.core.constant.expertise.SubstanceType;
import uz.maroqand.ecology.core.entity.expertise.Substance;

import java.util.List;

public interface SubstanceRepository extends JpaRepository<Substance, Integer>, JpaSpecificationExecutor<Substance> {
    List<Substance> findByDeletedFalse();
    List<Substance> findByDeletedFalseAndType(SubstanceType type);
}
