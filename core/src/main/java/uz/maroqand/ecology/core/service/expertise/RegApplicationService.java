package uz.maroqand.ecology.core.service.expertise;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.maroqand.ecology.core.constant.expertise.LogStatus;
import uz.maroqand.ecology.core.constant.expertise.LogType;
import uz.maroqand.ecology.core.dto.expertise.FilterDto;
import uz.maroqand.ecology.core.entity.billing.Invoice;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;
import uz.maroqand.ecology.core.entity.expertise.RegApplicationCategoryType;
import uz.maroqand.ecology.core.entity.expertise.RegApplicationInputType;
import uz.maroqand.ecology.core.entity.user.User;

import java.util.Date;
import java.util.List;

public interface RegApplicationService {

    Integer sendSMSCode(String mobilePhone,Integer regApplicationId);

    void update(RegApplication regApplication);

    RegApplication create(User user, RegApplicationInputType inputType, RegApplicationCategoryType categoryType);

    List<RegApplication> getByClientId(Integer id);

    List<RegApplication> getByClientIdDeletedFalse(Integer id);

    List<RegApplication> getByInvoiceId(Integer invoiceId);

    RegApplication getByOneInvoiceId(Integer invoiceId);

    RegApplication getTopByOneInvoiceId(Integer invoiceId);

    List<RegApplication> getAllByPerfomerIdNotNullDeletedFalse();

    RegApplication getById(Integer id);

    RegApplication getByIdAndUserTin(Integer id, User user);

    RegApplication sendRegApplicationAfterPayment(RegApplication regApplication, User user, Invoice invoice, String locale);

    RegApplication cancelApplicationByInvoiceId(Integer invoiceId);

    List<RegApplication> getListByPerformerId(Integer performerId);

    RegApplication getById(Integer id, Integer createdBy);

    Boolean beforeOrEqualsTrue(RegApplication regApplication);

    Page<RegApplication> findFiltered(FilterDto filterDto, Integer reviewId, LogType logType, Integer performerId, Integer userId, RegApplicationInputType regApplicationInputType,Pageable pageable);

}
