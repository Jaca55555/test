package uz.maroqand.ecology.core.entity.expertise;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import uz.maroqand.ecology.core.constant.expertise.Category;
import uz.maroqand.ecology.core.entity.billing.Invoice;
import uz.maroqand.ecology.core.entity.client.Client;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Utkirbek Boltaev on 10.06.2019.
 * (uz)
 */
@Data
@Entity
@Table(name = "reg_application")
public class RegApplication {

    @Transient
    private static final String sequenceName = "reg_application_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", insertable = false, updatable = false)
    private Client applicant;

    @Column(name = "applicant_id")
    private Integer applicantId;

    //Объект экспертизы
    /*@OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "object_id", insertable = false, updatable = false)
    private ObjectExpertise object;*/

    @Column(name = "object_id")
    private Integer objectId;

    //Вид деятельности
    /*@OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", insertable = false, updatable = false)
    private Activity activity;*/

    @Column(name = "activity_id")
    private Integer activityId;

    /*//
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", insertable = false, updatable = false)
    private Invoice invoice;*/

    @Column(name = "invoice_id")
    private Integer invoiceId;

    //Категория
    @Column(name = "category")
    @Enumerated(EnumType.ORDINAL)
    private Category category;

    //Вид материалов
    /*@OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", insertable = false, updatable = false)
    private Material material;*/

    @Column(name = "material_id")
    private Integer materialId;


    //Наименование объекта
    private String name;

    //разработчика проекта
    /*@OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "developer_id", insertable = false, updatable = false)
    private ProjectDeveloper developer;
*/
    @Column(name = "developer_id")
    private Integer developerId;

    //Ko'rib chiquvchi tashkilot
    /*@OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", insertable = false, updatable = false)
    private Organization review;*/

    @Column(name = "review_id")
    private Integer reviewId;

    //Kontrakt nomer
    //OrganizationService.getContractNumber
    @Column(name = "contract_number")
    private String contractNumber;

    /*@OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offer_id", insertable = false, updatable = false)
    private Offer offer;*/

    @Column(name = "offer_id")
    private Integer offerId;

    /*@OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requirement_id", insertable = false, updatable = false)
    private Requirement requirement;*/

    @Column(name = "requirement_id")
    private Integer requirementId;

    /*
     * Technical Fields
     */
    @Column(name = "deleted",columnDefinition = "boolean DEFAULT false")
    private Boolean deleted = false;

    @Column(name="created_at", columnDefinition = "timestamp without time zone")
    private Date createdAt;

    @Column(name="update_at", columnDefinition = "timestamp without time zone")
    private Date updateAt;

    /*@OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", insertable = false, updatable = false)
    private User createdBy;*/

    @Column(name = "created_by")
    private Integer createdById;

    /*@OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "update_by", insertable = false, updatable = false)
    private User updateBy;*/

    @Column(name = "update_by")
    private Integer updateById;

}
