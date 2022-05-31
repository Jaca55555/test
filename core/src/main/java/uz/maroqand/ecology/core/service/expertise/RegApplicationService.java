package uz.maroqand.ecology.core.service.expertise;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.maroqand.ecology.core.constant.expertise.Category;
import uz.maroqand.ecology.core.constant.expertise.LogStatus;
import uz.maroqand.ecology.core.constant.expertise.LogType;
import uz.maroqand.ecology.core.constant.expertise.RegApplicationStatus;
import uz.maroqand.ecology.core.dto.api.RegApplicationDTO;
import uz.maroqand.ecology.core.dto.expertise.FilterDto;
import uz.maroqand.ecology.core.entity.billing.Invoice;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;
import uz.maroqand.ecology.core.entity.expertise.RegApplicationCategoryType;
import uz.maroqand.ecology.core.entity.expertise.RegApplicationInputType;
import uz.maroqand.ecology.core.entity.user.User;
import java.util.Date;
import java.util.List;
import java.util.Set;

public interface RegApplicationService {

    Integer sendSMSCode(String mobilePhone,Integer regApplicationId);

    void update(RegApplication regApplication);
    void cancelModification();
    void closeModificationTimer();

    RegApplication create(User user, RegApplicationInputType inputType, RegApplicationCategoryType categoryType);
    RegApplication getByOfferId(Integer offerId);
    List<RegApplication> getByClientId(Integer id);

    List<RegApplication> getByClientIdDeletedFalse(Integer id);

    List<RegApplication> findByDelivered();

    List<RegApplication> getByInvoiceId(Integer invoiceId);

    RegApplication getByOneInvoiceId(Integer invoiceId);

    RegApplication getTopByOneInvoiceId(Integer invoiceId);
    RegApplication updateBoiler(RegApplication regApplication,Integer userId);
    List<RegApplication> getAllByPerfomerIdNotNullDeletedFalse();

    RegApplication getById(Integer id);
    RegApplication getByContractNumber(String contractNumber);
    RegApplication getByIdAndUserTin(Integer id, User user);
//    List<RegApplicationDTO> listByTin(Integer tin);
    RegApplication sendRegApplicationAfterPayment(RegApplication regApplication, User user, Invoice invoice, String locale);

    RegApplication cancelApplicationByInvoiceId(Integer invoiceId);

    List<RegApplication> getListByPerformerId(Integer performerId);

    RegApplication getById(Integer id, Integer createdBy);

    Boolean beforeOrEqualsTrue(RegApplication regApplication);

    Page<RegApplication> findFiltered(FilterDto filterDto, Integer reviewId, LogType logType, Integer performerId, Integer userId, RegApplicationInputType regApplicationInputType,Pageable pageable);
    Integer countByCategoryAndStatusAndRegionId(Category category,Date dateBegin,Date dateEnd, RegApplicationStatus status, Integer regionId, Set<Integer> organizationIds);
    Integer countByCategoryAndStatusAndSubRegionId(Category category,Date dateBegin,Date dateEnd, RegApplicationStatus status,Integer subRegionId,Set<Integer> organizationIds);
}
