package uz.maroqand.ecology.core.repository.sys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.core.entity.sys.AppealSub;

import java.util.List;

@Repository
public interface AppealSubRepository extends JpaRepository<AppealSub,Integer> {

    List<AppealSub> findByAppealIdAndDeletedFalseOrderByIdAsc(Integer id);

}
