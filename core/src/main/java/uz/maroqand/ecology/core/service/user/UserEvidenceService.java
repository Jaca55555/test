package uz.maroqand.ecology.core.service.user;

import javafx.scene.effect.SepiaTone;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.maroqand.ecology.core.entity.sys.File;
import uz.maroqand.ecology.core.entity.user.Department;
import uz.maroqand.ecology.core.entity.user.EvidinceStatus;
import uz.maroqand.ecology.core.entity.user.UserEvidence;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface UserEvidenceService {

    //for file upload or delete
    UserEvidence save(UserEvidence userEvidence);

    List<UserEvidence> getListByUserId(Integer userId);

    UserEvidence getById(Integer id);



}
