package uz.maroqand.ecology.core.service.user;


import uz.maroqand.ecology.core.constant.user.ToastrType;
import uz.maroqand.ecology.core.dto.user.Toastr;

import java.util.List;

/**
 * Created by Utkirbek Boltaev on 03.06.2019.
 * (uz)
 * (ru)
 */
public interface ToastrService {

    void initialization();

    List<Toastr> getByUserId(Integer userId);

    void create(Integer userId, ToastrType toastrType, String title, String message);

}
