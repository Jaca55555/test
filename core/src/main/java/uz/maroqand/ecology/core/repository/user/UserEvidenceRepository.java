package uz.maroqand.ecology.core.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.core.entity.user.UserEvidence;

import java.util.List;

@Repository
public interface UserEvidenceRepository extends JpaRepository<UserEvidence, Integer>, JpaSpecificationExecutor<UserEvidence> {

    List<UserEvidence> findByUserId(Integer userId);

}
