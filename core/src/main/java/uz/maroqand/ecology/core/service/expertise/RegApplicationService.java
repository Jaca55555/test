package uz.maroqand.ecology.core.service.expertise;

import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;
import uz.maroqand.ecology.core.entity.user.User;

public interface RegApplicationService {

    RegApplication create(User user);

    RegApplication getById(Integer id);

    RegApplication getById(Integer id, Integer createdBy);

    DataTablesOutput<RegApplication> findFiltered(DataTablesInput input, Integer userId);

}
