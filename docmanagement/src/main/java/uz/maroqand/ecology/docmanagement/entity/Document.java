package uz.maroqand.ecology.docmanagement.entity;

import lombok.Data;
import uz.maroqand.ecology.core.entity.sys.File;
import uz.maroqand.ecology.core.entity.sys.Organization;
import uz.maroqand.ecology.docmanagement.constant.ControlForm;
import uz.maroqand.ecology.docmanagement.constant.DocumentStatus;
import uz.maroqand.ecology.docmanagement.constant.ExecuteForm;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

/**
 * Created by Utkirbek Boltaev on 29.03.2019.
 * (uz) Kiruvchi va chiquvchi hujjatlarni ro'yxatdan o'tkazish uchun
 * (ru)
 */
@Data
@Entity
@Table(name = "document")
public class Document {

    @Transient
    private static final String sequenceName = "document_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;

    //xat qaysi tashkilotga tegishli ekanligi(tizimdan foydalanuvchi tashkilotlardan)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", insertable = false, updatable = false)
    private Organization organization;

    @Column(name = "organization_id")
    private Integer organizationId;

    //kiruvchi, chiquvchi, ichki xujjat
    //Тип документа
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_type_id", insertable = false, updatable = false)
    private DocumentType documentType;

    @Column(name = "document_type_id")
    private Integer documentTypeId;

    //Журнал регистраци
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "journal_id", insertable = false, updatable = false)
    private Journal journal;

    @Column(name = "journal_id")
    private Integer journalId;

    //Хужжат тури
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_view_id", insertable = false, updatable = false)
    private DocumentView documentView;

    @Column(name = "document_view_id")
    private Integer documentViewId;

    //hujjat statusi
    @Column(name = "status")
    private DocumentStatus status;

    //registratsiya sanasi(tizim tomonidan belgilanadi)
    @Column(name = "registration_date", columnDefinition = "timestamp without time zone")
    private Date registrationDate;

    //registratsiya raqami(tizim tomonidan belgilanadi)
    @Column(name = "registration_number")
    private String registrationNumber;



    //kiruvchi hujjat yoki qaror sanasi
    @Column(name = "doc_reg_date", columnDefinition = "timestamp without time zone")
    private Date docRegDate;

    //kiruvchi hujjat yoki qaror raqami
    @Column(name = "doc_reg_number")
    private String docRegNumber;

    @Column(name = "content_id")
    private Integer contentId;

    @Column(name = "content")
    private String content;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "document_jt_content_files",
            joinColumns = { @JoinColumn(name = "appeal_id") },
            inverseJoinColumns = { @JoinColumn(name = "file_id") })
    private Set<File> contentFiles;



    //Ушбу хатга қўшимча тариқасида юборилган (агар мавжуд бўлса)
    @Column(name = "additional_document_id")
    private Integer additionalDocumentId;

    //маъсул ходимнинг исми ва фамилияси
    @Column(name = "performer_name")
    private String performerName;

    //Маъсул ходим билан алоқа учун телефон рақами
    @Column(name = "performer_phone")
    private String performerPhone;



    //Хатнинг ижроси шакли
    @Column(name = "execute_form")
    private ExecuteForm executeForm;

    //Назорат карточкасининг шакли
    @Column(name = "control_form")
    private ControlForm controlForm;

    //Для внутренного использования
    //Хужжат фақат хизмат доирасида фойдаланиш учун мулжалланган
    @Column(name = "inside_purpose", columnDefinition = "boolean DEFAULT false")
    private Boolean insidePurpose;

    //Кирувчи хатни қабул қилиш учун маъсул рахбар
    @Column(name = "manager_id")
    private Integer managerId;

    //Ижро назорати учун маъсул шахс
    @Column(name = "control_id")
    private Integer controlId;

    /*
     * Technical Fields
     */
    @Column(name = "deleted",columnDefinition = "boolean DEFAULT false")
    private Boolean deleted = false;

    @Column(name = "created_by_id")
    private Integer createdById;

    @Column(name="created_at", columnDefinition = "timestamp without time zone")
    private Date createdAt;

    @Column(name = "update_by_id")
    private Integer updateById;

    @Column(name="update_at", columnDefinition = "timestamp without time zone")
    private Date updateAt;

    @Column(name = "department_id")
    private Integer departmentId;
}
