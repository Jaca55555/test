package uz.maroqand.ecology.core.repository.expertise;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.core.constant.expertise.LogStatus;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;

import java.util.List;

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

    RegApplication findByIdAndCreatedByIdAndDeletedFalse(Integer id, Integer createdBy);
}
