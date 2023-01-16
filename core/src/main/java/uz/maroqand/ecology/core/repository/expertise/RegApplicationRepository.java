package uz.maroqand.ecology.core.repository.expertise;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.core.constant.expertise.Category;
import uz.maroqand.ecology.core.constant.expertise.LogStatus;
import uz.maroqand.ecology.core.constant.expertise.RegApplicationStatus;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;
import uz.maroqand.ecology.core.entity.client.Client;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by Utkirbek Boltaev on 10.06.2019.
 * (uz)
 */
@Repository
public interface RegApplicationRepository extends DataTablesRepository<RegApplication, Integer>, JpaRepository<RegApplication, Integer>, JpaSpecificationExecutor<RegApplication> {

    RegApplication findByIdAndDeletedFalse(Integer id);
    List<RegApplication> findByDeletedFalse();
    RegApplication findByContractNumber(String contractNumber);
    List<RegApplication> findByApplicantId(Integer id);

    List<RegApplication> findByApplicantIdAndDeletedFalse(Integer id);
    @Query(value = "SELECT * from reg_application r where r.delivery_status=0 order by r.id ASC limit 100",nativeQuery = true)
    List<RegApplication> findAllByDeliveryStatusAndDeletedFalse(Short deliveryStatus);

    List<RegApplication> findByInvoiceId(Integer invoiceId);
    RegApplication findByOfferIdAndDeletedFalse(Integer offerId);
    RegApplication findByInvoiceIdAndDeletedFalse(Integer invoiceId);
    RegApplication findTop1ByInvoiceIdAndDeletedFalse(Integer invoiceId);
    List<RegApplication> findAllByPerformerIdNotNullAndDeletedFalseOrderByIdDesc();
    List<RegApplication> findAllByStatusInAndDeletedFalseOrderByIdDesc(List<RegApplicationStatus> status);

    List<RegApplication> findByCreatedByIdAndStatus(Integer id, RegApplicationStatus status);

    List<RegApplication> findAllByPerformerIdAndDeletedFalseOrderByIdDesc(Integer performerId);
//    Status!=null
    @Query("SELECT COUNT(r) FROM RegApplication r where r.category=:category and r.deadlineDate>:dateBegin and r.deadlineDate<:dateEnd and r.status=:status and r.applicant.regionId=:regionId and r.reviewId in :organizationIds")
    Integer countByCategoryAndStatusAndRegionId(@Param("category")Category category, @Param("dateBegin") Date dateBegin,@Param("dateEnd") Date dateEnd, @Param("status")RegApplicationStatus status, @Param("regionId")Integer regionId, @Param("organizationIds")Set<Integer> organizationIds);
    @Query("SELECT COUNT(r) FROM RegApplication r where r.category=:category and r.deadlineDate>:dateBegin and r.deadlineDate<:dateEnd and r.status=:status and r.applicant.subRegionId=:subRegionId and r.reviewId in :organizationIds")
    Integer countByCategoryAndStatusAndSubRegionId(@Param("category")Category category,@Param("dateBegin") Date dateBegin,@Param("dateEnd") Date dateEnd,@Param("status")RegApplicationStatus status,@Param("subRegionId")Integer subRegionId, @Param("organizationIds")Set<Integer> organizationIds);
//    Category!=null
    @Query("SELECT COUNT(r) FROM RegApplication r where  r.deadlineDate>:dateBegin and r.deadlineDate<:dateEnd and r.status=:status and r.applicant.regionId=:regionId and r.reviewId in :organizationIds")
    Integer countByStatusAndRegionId( @Param("dateBegin") Date dateBegin,@Param("dateEnd") Date dateEnd, @Param("status")RegApplicationStatus status, @Param("regionId")Integer regionId, @Param("organizationIds")Set<Integer> organizationIds);
    @Query("SELECT COUNT(r) FROM RegApplication r where r.deadlineDate>:dateBegin and r.deadlineDate<:dateEnd and r.status=:status and r.applicant.subRegionId=:subRegionId and r.reviewId in :organizationIds")
    Integer countByStatusAndSubRegionId(@Param("dateBegin") Date dateBegin,@Param("dateEnd") Date dateEnd,@Param("status")RegApplicationStatus status,@Param("subRegionId")Integer subRegionId, @Param("organizationIds")Set<Integer> organizationIds);



    //    Status==null
    @Query("SELECT COUNT(r) FROM RegApplication r where r.category=:category and r.deadlineDate>:dateBegin and r.deadlineDate<:dateEnd  and r.applicant.regionId=:regionId and r.reviewId in :organizationIds")
    Integer countByCategoryAndRegionId(@Param("category")Category category, @Param("dateBegin") Date dateBegin,@Param("dateEnd") Date dateEnd, @Param("regionId")Integer regionId, @Param("organizationIds")Set<Integer> organizationIds);
    @Query("SELECT COUNT(r) FROM RegApplication r where r.category=:category and r.deadlineDate>:dateBegin and r.deadlineDate<:dateEnd and r.applicant.subRegionId=:subRegionId and r.reviewId in :organizationIds")
    Integer countByCategoryAndSubRegionId(@Param("category")Category category,@Param("dateBegin") Date dateBegin,@Param("dateEnd") Date dateEnd,@Param("subRegionId")Integer subRegionId, @Param("organizationIds")Set<Integer> organizationIds);
//    Category!=null
    @Query("SELECT COUNT(r) FROM RegApplication r where r.deadlineDate>:dateBegin and r.deadlineDate<:dateEnd  and r.applicant.regionId=:regionId and r.reviewId in :organizationIds")
    Integer countByRegionId( @Param("dateBegin") Date dateBegin,@Param("dateEnd") Date dateEnd, @Param("regionId")Integer regionId, @Param("organizationIds")Set<Integer> organizationIds);
    @Query("SELECT COUNT(r) FROM RegApplication r where r.deadlineDate>:dateBegin and r.deadlineDate<:dateEnd and r.applicant.subRegionId=:subRegionId and r.reviewId in :organizationIds")
    Integer countBySubRegionId(@Param("dateBegin") Date dateBegin,@Param("dateEnd") Date dateEnd,@Param("subRegionId")Integer subRegionId, @Param("organizationIds")Set<Integer> organizationIds);


    RegApplication findByIdAndCreatedByIdAndDeletedFalse(Integer id, Integer createdBy);

    List<RegApplication> findAllByReviewIdAndStatusAndDeletedFalse(Integer organizationId, RegApplicationStatus process);

    Integer countByPerformerIdAndCategoryAndDeletedFalseAndRegistrationDateBetween(Integer performerId, Category category,Date beginDate,Date endDate);
    Integer countByPerformerIdAndDeletedFalseAndRegistrationDateBetween(Integer performerId, Date beginDate,Date endDate);
}
