package uz.maroqand.ecology.core.service.expertise;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.maroqand.ecology.core.constant.expertise.Category;
import uz.maroqand.ecology.core.constant.expertise.DidoxStatus;
import uz.maroqand.ecology.core.constant.expertise.LogType;
import uz.maroqand.ecology.core.constant.expertise.RegApplicationStatus;
import uz.maroqand.ecology.core.dto.expertise.FilterDto;
import uz.maroqand.ecology.core.entity.billing.Invoice;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;
import uz.maroqand.ecology.core.entity.expertise.RegApplicationCategoryType;
import uz.maroqand.ecology.core.entity.expertise.RegApplicationInputType;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.repository.DidoxRepository;

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
    RegApplication findByInvoiceIdAndDeletedFalse(Integer invoiceId);
    List<RegApplication> getByClientId(Integer id);

    List<RegApplication> getByClientIdDeletedFalse(Integer id);

    List<RegApplication> getByDidoxStatusAndDeletedFalse(DidoxStatus status);

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
    Integer countDeadlineRegApplication();
    Integer countNewRegApplication();

    String getUserKey();
    Integer countRegApplicationByPerformerIdAndRegistrationDateBetween(Integer userId,Date beginDate,Date endDate);

    List<RegApplication> getListByPerformerId(Integer performerId);
    Integer countByPerformerIdAndCategory(Integer performerId, Category category,Date beginDate,Date endDate);

    RegApplication getById(Integer id, Integer createdBy);

    Boolean beforeOrEqualsTrue(RegApplication regApplication);

    Page<RegApplication> findFiltered(FilterDto filterDto, Integer reviewId, LogType logType, Integer performerId, Integer userId, RegApplicationInputType regApplicationInputType,Pageable pageable);
    Page<RegApplication> findFilteredExcel(FilterDto filterDto, Integer reviewId, LogType logType, Integer performerId, Integer userId, RegApplicationInputType regApplicationInputType,Pageable pageable);
    Integer countByCategoryAndStatusAndRegionId(Category category,Date dateBegin,Date dateEnd, RegApplicationStatus status, Integer regionId, Set<Integer> organizationIds);
    Integer countByCategoryAndStatusAndSubRegionId(Category category,Date dateBegin,Date dateEnd, RegApplicationStatus status,Integer subRegionId,Set<Integer> organizationIds);

    long findFilteredNumber(LogType logType, Integer integer, Integer performerId, RegApplicationInputType regApplicationInputType);

    List<RegApplication> getListByStatusAndUserId(RegApplicationStatus checkNotConfirmed, Integer id);

    boolean reWorkRegApplication(Integer id, Integer reId, User user);
}
