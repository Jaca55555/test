package uz.maroqand.ecology.core.entity.expertise;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import uz.maroqand.ecology.core.constant.expertise.*;
import uz.maroqand.ecology.core.entity.client.Client;
import uz.maroqand.ecology.core.entity.sys.File;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

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

    @Column(name = "applicant_type")
    @Enumerated(EnumType.ORDINAL)
    private ApplicantType applicantType;

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

    //Категория
    @Column(name = "category")
    @Enumerated(EnumType.ORDINAL)
    private Category category;

    //Вид материалов
    /*@OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", insertable = false, updatable = false)
    private Material material;*/

    /*@Column(name = "material_id")
    private Integer materialId;*/

    @ElementCollection(targetClass = Integer.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "reg_application_jt_material",
            joinColumns = @JoinColumn(name = "reg_application_id"))
    @Column(name = "material_id")
    private Set<Integer> materials;

    //Наименование объекта
    private String name;

    //разработчика проекта
    /*@OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "developer_id", insertable = false, updatable = false)
    private ProjectDeveloper developer;
*/
    @Column(name = "developer_id")
    private Integer developerId;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "reg_application_jt_document_files",
            joinColumns = { @JoinColumn(name = "reg_application_id") },
            inverseJoinColumns = { @JoinColumn(name = "file_id") })
    private Set<File> documentFiles;

    //Kontrakt nomer
    //OrganizationService.getContractNumber
    @Column(name = "contract_number")
    private String contractNumber;

    @Column(name = "contract_date")
    private Date contractDate;

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

    private Integer deadline;

    //Дата рег.
    @Column(name = "registration_date")
    private Date registrationDate;

    //Срок исполнения
    @Column(name = "deadline_date")
    private Date deadlineDate;

    //To'lov uchun invoice
    /*@OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", insertable = false, updatable = false)
    private Invoice invoice;*/

    @Column(name = "invoice_id")
    private Integer invoiceId;

    //Ko'rib chiquvchi tashkilot
    /*@OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", insertable = false, updatable = false)
    private Organization review;*/

    @Column(name = "review_id")
    private Integer reviewId;

    //Исполнитель
    /*@OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performer_id", insertable = false, updatable = false)
    private User performer;*/

    @Column(name = "performer_id")
    private Integer performerId;

    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    private RegApplicationStatus status;

    @Column(name = "step")
    @Enumerated(EnumType.ORDINAL)
    private RegApplicationStep step;

    //true - byudjet tashkilot
    @Column(name = "budget",columnDefinition = "boolean")
    private Boolean budget;

    @Column(name = "checked_sms_id")
    private Integer checkedSmsId;

    /*
    * oxirgi log id lar saqlanadi
    * */
    @Column(name = "confirm_log_id")
    private Integer confirmLogId;

    @Column(name = "forwarding_log_id")
    private Integer forwardingLogId;

    @Column(name = "performer_log_id")
    private Integer performerLogId;

    /*@Column(name = "agreement_log_id")
    private Integer agreementLogId;*/

    @ElementCollection(targetClass = Integer.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "reg_application_jt_agreement_logs",
            joinColumns = @JoinColumn(name = "reg_application_id"))
    @Column(name = "log_id")
    private Set<Integer> agreementLogs;

    @Column(name = "agreement_complete_log_id")
    private Integer agreementCompleteLogId;

    /*
     * Technical Fields
     */

    //for cancel
    private String message;


    @Column(name = "facture",columnDefinition = "boolean DEFAULT false")
    private Boolean facture = false;

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
