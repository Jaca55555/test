package uz.maroqand.ecology.core.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.core.entity.user.UserAdditional;

import java.util.List;

/**
 * Created by Utkirbek Boltaev on 10.06.2019.
 * (uz)
 */
@Repository
public interface UserAdditionalRepository extends JpaRepository<UserAdditional, Integer>, JpaSpecificationExecutor<UserAdditional> {

    List<UserAdditional> findTop25ByUserId(Integer userId);

}
