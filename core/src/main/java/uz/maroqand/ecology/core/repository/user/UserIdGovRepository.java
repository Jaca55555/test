package uz.maroqand.ecology.core.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.core.entity.user.UserIdGov;

/**
 * Created by Utkirbek Boltaev on 17.07.2019.
 * (uz)
 * (ru)
 */
@Repository
public interface UserIdGovRepository extends JpaRepository<UserIdGov, Integer> {


}
