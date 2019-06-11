package uz.maroqand.ecology.core.service.sys;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.maroqand.ecology.core.entity.sys.Appeal;
import uz.maroqand.ecology.core.entity.user.User;

import java.util.Date;

/**
 * Created by Utkirbek Boltaev on 20.05.2019.
 * (uz)
 */
public interface AppealService {

    void updateByUserId(Integer userId);

    Appeal create(Appeal appeal,User user);

    Appeal update(Appeal appeal,User user);

    Appeal updateCommentCount(Appeal appeal);

    Appeal delete(Appeal appeal,User user);

    Appeal getById(Integer id,Integer createdById);

    Page<Appeal> findFiltered(
            Integer appealId,
            Integer appealType,
            String title,
            Date dateBegin,
            Date dateEnd,
            Integer status,
            Integer createdBy,
            Pageable pageable
    );

}
