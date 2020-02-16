package uz.maroqand.ecology.docmanagement.entity;

import lombok.Data;
import uz.maroqand.ecology.docmanagement.constant.DocumentSubType;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Utkirbek Boltaev on 30.04.2019.
 * (uz)
 * (ru)
 */

@Data
@Entity
@Table(name = "document_sub")
public class DocumentSub {

    @Transient
    private static final String sequenceName = "document_sub_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;

    //Document.ID
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", insertable = false, updatable = false)
    private Document document;

    @Column(name = "document_id")
    private Integer documentId;

    @Column(name = "type")
    private DocumentSubType type;

    //Хатни жўнатиш усули (почта орқали, e-mail, факс, етказиб бериш ва х.к.)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "communication_tool_id", insertable = false, updatable = false)
    private CommunicationTool communicationTool;

    //CommunicationTool.ID
    @Column(name = "communication_tool_id")
    private Integer communicationToolId;

    //Xat qabul qiluvchi. Organization
    //Хат юборилаётган ташкилот
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", insertable = false, updatable = false)
    private DocumentOrganization organization;

    //DocumentOrganization.id
    @Column(name = "organization_id")
    private Integer organizationId;

    //DocumentOrganization.name
    @Column(name = "organization_name")
    private String organizationName;

    //Xat qabul qiluvchi. FIO
    //Хат юборилаётган шахс
    @Column(name = "full_name")
    private String fullName;

    //Xat qabul qiluvchi. Lavozimi
    //Хат юборилаётган ходимнинг лавозими
    @Column(name = "position")
    private String position;

    //Xat qabul qiluvchi. Adreis
    //Адресат манзили
    @Column(name = "address")
    private String address;

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
}
