package uz.maroqand.ecology.core.entity.expertise;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vladmihalcea.hibernate.type.array.IntArrayType;
import com.vladmihalcea.hibernate.type.array.StringArrayType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import uz.maroqand.ecology.core.constant.expertise.*;
import uz.maroqand.ecology.core.entity.client.Client;
import uz.maroqand.ecology.core.entity.sys.File;
import uz.maroqand.ecology.core.entity.sys.Organization;
import uz.maroqand.ecology.core.entity.sys.Soato;
import uz.maroqand.ecology.core.entity.user.User;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by Utkirbek Boltaev on 10.06.2019.
 * (uz)
 */
@Data
@Entity
@Table(name = "reg_application")
@TypeDefs({
        @TypeDef(name = "string-array", typeClass = StringArrayType.class),
        @TypeDef(name = "int-array", typeClass = IntArrayType.class),
        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
})
@AllArgsConstructor
@NoArgsConstructor
public class RegApplication {

    @Transient
    private static final String sequenceName = "reg_application_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "applicant_id", insertable = false, updatable = false)
    private Client applicant;


    @Column(name = "applicant_id")
    private Integer applicantId;

    @Column(name = "applicant_type")
    @Enumerated(EnumType.ORDINAL)
    private ApplicantType applicantType;

    @Column(name = "input_type")
    @Enumerated(EnumType.ORDINAL)
    private RegApplicationInputType inputType;

    // category 4 uchun
    @Column(name = "reg_application_category_type")
    @Enumerated(EnumType.ORDINAL)
    private RegApplicationCategoryType regApplicationCategoryType;

    //Объект экспертизы
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "object_id", insertable = false, updatable = false)
    private ObjectExpertise object;

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

    //Регион
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", updatable = false, insertable = false)
    private Soato region;

    @Column(name = "region_id")
    private Integer regionId;

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
    //    Object location
    @Column(name = "object_region_id")
    private Integer objectRegionId;


    @Column(name = "object_sub_region_id")
    private Integer objectSubRegionId;

    private String individualPhone;
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

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "reg_application_jt_contract_files",
            joinColumns = { @JoinColumn(name = "reg_application_id") },
            inverseJoinColumns = { @JoinColumn(name = "file_id") })
    private Set<File> contractFiles;

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
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "review_id", insertable = false, updatable = false)
    private Organization review;

    @Column(name = "review_id")
    private Integer reviewId;

    //
    @Column(name = "log_index")
    private Integer logIndex;

    //Исполнитель
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "performer_id", insertable = false, updatable = false)
    private User performer;

    @Column(name = "performer_id")
    private Integer performerId;

    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    private RegApplicationStatus status;

    @Column(name = "agreement_status")
    @Enumerated(EnumType.ORDINAL)
    private LogStatus agreementStatus;

    @Column(name = "step")
    @Enumerated(EnumType.ORDINAL)
    private RegApplicationStep step;

    @Column(name = "category_four_step")
    @Enumerated(EnumType.ORDINAL)
    private RegApplicationCategoryFourStep categoryFourStep;

    @Column(name = "conclusion_online",columnDefinition = "boolean DEFAULT true")
    private Boolean conclusionOnline = true;

    @Column(name = "conclusion_id")
    private Integer conclusionId;

    //true - byudjet tashkilot
    @Column(name = "budget",columnDefinition = "boolean")
    private Boolean budget;

    @Column(name = "checked_sms_id")
    private Integer checkedSmsId;

    /*
    * oxirgi log id lar saqlanadi
    * */
    //tasdiqlashga yuborilgan sana
    @Column(name="confirm_log_at", columnDefinition = "timestamp without time zone")
    private Date confirmLogAt;

    @Column(name = "confirm_log_id")
    private Integer confirmLogId;

    @Column(name = "forwarding_log_id")
    private Integer forwardingLogId;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forwarding_log_id", insertable = false, updatable = false)
    private RegApplicationLog forwardingLog;

    @JsonIgnore
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "performer_log_id", insertable = false, updatable = false)
    private RegApplicationLog performerLog;


    @Column(name = "performer_log_id")
    private Integer performerLogId;

    @Column(name = "performer_log_id_next")
    private Integer performerLogIdNext;

    /*@Column(name = "agreement_log_id")
    private Integer agreementLogId;*/

    @ElementCollection(targetClass = Integer.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "reg_application_jt_agreement_logs",
            joinColumns = @JoinColumn(name = "reg_application_id"))
    @Column(name = "log_id")
    private Set<Integer> agreementLogs;

    @Column(name = "agreement_complete_log_id")
    private Integer agreementCompleteLogId;

    //xulosaga number va sana qo'yish
    @Column(name = "conclusion_complete_log_id")
    private Integer conclusionCompleteLogId;

    /*
     * Technical Fields
     */

    //for cancel
    private String message;

    @Column(name = "facture_id")
    private Integer factureId;

    @Column(name = "facture",columnDefinition = "boolean DEFAULT false")
    private Boolean facture = false;

    //for nds uchun
    @Column(name = "add_nds",columnDefinition = "boolean DEFAULT false")
    private Boolean addNds ;

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

//    @ManyToMany(fetch = FetchType.LAZY)
//    @JoinTable(name = "reg_application_jt_substance_content",
//            joinColumns = { @JoinColumn(name = "reg_application_id") },
//            inverseJoinColumns = { @JoinColumn(name = "substance_content_id") })
//    private Set<SubstanceContent> substanceContents;
    //Qozon nomi
    @Column(name = "boiler_name")
    private String boilerName;

    //Қозон характеристикаси
    @ManyToMany
    @JoinTable(name = "reg_application_jt_boiler_characteristics",
            joinColumns = { @JoinColumn(name = "reg_application") },
            inverseJoinColumns = { @JoinColumn(name = "boiler_characteristics")})
    @OrderBy(value = "id asc")
    private Set<BoilerCharacteristics> boilerCharacteristics;


    @Column(name = "delivery_status")
    private Short deliveryStatus;

    @Type(type = "jsonb")
    @Column(name = "boiler_groups", columnDefinition = "jsonb")
    private List<BoilerGroupEnum> boilerGroups;

    @Column(name = "didox_id")
    private String didoxId;

    @Column(name = "didox_status")
    private DidoxStatus didoxStatus;
}
