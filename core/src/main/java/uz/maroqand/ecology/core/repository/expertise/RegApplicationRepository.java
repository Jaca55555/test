package uz.maroqand.ecology.core.repository.expertise;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
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
import java.util.List;
import java.util.Set;

/**
 * Created by Utkirbek Boltaev on 10.06.2019.
 * (uz)
 */
@Repository
public interface RegApplicationRepository extends DataTablesRepository<RegApplication, Integer>, JpaRepository<RegApplication, Integer>, JpaSpecificationExecutor<RegApplication> {

    RegApplication findByIdAndDeletedFalse(Integer id);
    RegApplication findByContractNumber(String contractNumber);
    List<RegApplication> findByApplicantId(Integer id);

    List<RegApplication> findByApplicantIdAndDeletedFalse(Integer id);

    List<RegApplication> findByInvoiceId(Integer invoiceId);
    RegApplication findByOfferIdAndDeletedFalse(Integer offerId);
    RegApplication findByInvoiceIdAndDeletedFalse(Integer invoiceId);
    RegApplication findTop1ByInvoiceIdAndDeletedFalse(Integer invoiceId);
    List<RegApplication> findAllByPerformerIdNotNullAndDeletedFalseOrderByIdDesc();

    List<RegApplication> findAllByPerformerIdAndDeletedFalseOrderByIdDesc(Integer performerId);
    @Query("SELECT COUNT(r) FROM RegApplication r where r.category=:category and r.status=:status and r.applicant.regionId=:regionId and r.reviewId in :organizationIds")
    Integer countByCategoryAndStatusAndRegionId(@Param("category")Category category,@Param("status")RegApplicationStatus status,@Param("regionId")Integer regionId, @Param("organizationIds")Set<Integer> organizationIds);
    @Query("SELECT COUNT(r) FROM RegApplication r where r.category=:category and r.status=:status and r.applicant.subRegionId=:subRegionId and r.reviewId in :organizationIds")
    Integer countByCategoryAndStatusAndSubRegionId(@Param("category")Category category,@Param("status")RegApplicationStatus status,@Param("subRegionId")Integer subRegionId, @Param("organizationIds")Set<Integer> organizationIds);
    RegApplication findByIdAndCreatedByIdAndDeletedFalse(Integer id, Integer createdBy);
}
