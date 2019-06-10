package uz.maroqand.ecology.core.service.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.maroqand.ecology.core.entity.user.User;

/**
 * Created by Utkirbek Boltaev on 20.05.2019.
 * (uz)
 * (ru)
 */
public interface UserService {


    User findById(Integer id);

    User createUser(User user);

    User updateUser(User user);

    Page<User> findFiltered(
            Integer userId,
            String userName,
            Pageable pageable
    );

    public User findByUsername(String username);

    public User getCurrentUserFromContext();

}
