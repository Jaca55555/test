package uz.maroqand.ecology.docmanagement.entity;

import lombok.Data;
import uz.maroqand.ecology.core.entity.sys.File;

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

    @OneToOne(mappedBy = "document", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private DocumentSub documentSub;

    @OneToOne(mappedBy = "document", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private DocumentTask task;

    //registratsiya sanasi
    @Column(name = "registration_date", columnDefinition = "timestamp without time zone")
    private Date registrationDate;

    //registratsiya raqami
    @Column(name = "registration_number")
    private String registrationNumber;

    //Краткое содержание документа
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "document_description_id", nullable = false, updatable = false)
    private DocumentDescription documentDescription;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "document_jt_content_files",
            joinColumns = { @JoinColumn(name = "appeal_id") },
            inverseJoinColumns = { @JoinColumn(name = "file_id") })
    private Set<File> contentFiles;


    //Ушбу хатга қўшимча тариқасида юборилган (агар мавжуд бўлса)
    @Column(name = "additional_document_id")
    private Integer additionalDocumentId;

    //Ушбу хатга жавоб тариқасида юборилган (агар мавжуд бўлса)
    @Column(name = "answer_document_id")
    private Integer answerDocumentId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answer_document_id", insertable = false, updatable = false)
    private Document answerDocument;

    //Для внутренного использования
    @Column(name = "inside_purpose", columnDefinition = "boolean DEFAULT false")
    private Boolean insidePurpose;

    //маъсул ходимнинг исми ва фамилияси
    @Column(name = "performer_name")
    private String performerName;

    //Маъсул ходим билан алоқа учун телефон рақами
    @Column(name = "performer_phone")
    private String performerPhone;


    //restricted = true, Ko'rish kechlangan
    @Column(name = "restricted", columnDefinition = "boolean DEFAULT true")
    private Boolean restricted;

    @Column(name = "execute_form")
    private String executeForm;

    @Column(name = "control_form")
    private String controlForm;

    @Column(name = "special_control", columnDefinition = "boolean DEFAULT false")
    private Boolean specialControl;

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
