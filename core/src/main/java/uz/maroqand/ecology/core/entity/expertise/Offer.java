package uz.maroqand.ecology.core.entity.expertise;

import lombok.Data;
import uz.maroqand.ecology.core.entity.sys.Organization;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Utkirbek Boltaev on 15.06.2019.
 * (uz)
 * (ru)
 */
@Data
@Entity
@Table(name = "offer")
public class Offer {

    @Transient
    private static final String sequenceName = "offer_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;

    private String name;

    //file format doc, docx
    @Column(name = "file_uz")
    private Integer fileUzId;

    @Column(name = "file_oz")
    private Integer fileOzId;

    @Column(name = "file_ru")
    private Integer fileRuId;

    //true - byudjet tashkilot
    @Column(name = "byudjet",columnDefinition = "boolean DEFAULT false")
    private Boolean byudjet = false;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", insertable = false, updatable = false)
    private Organization organization;

    @Column(name = "organization_id")
    private Integer organizationId;

    /*
     * Technical Fields
     */
    @Column(name = "active",columnDefinition = "boolean DEFAULT false")
    private Boolean active = false;

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