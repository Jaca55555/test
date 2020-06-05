package uz.maroqand.ecology.core.entity.expertise;

import lombok.Data;
import uz.maroqand.ecology.core.constant.expertise.ConclusionStatus;
import uz.maroqand.ecology.core.entity.sys.DocumentRepo;
import uz.maroqand.ecology.core.entity.sys.File;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

/**
 * Created by Utkirbek Boltaev on 09.07.2019.
 * (uz)
 * (ru)
 */
@Data
@Entity
@Table(name = "conclusion")
public class Conclusion {

    @Transient
    private static final String sequenceName = "conclusion_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reg_application_id", insertable = false, updatable = false)
    private RegApplication regApplication;

    @Column(name = "reg_application_id")
    private Integer regApplicationId;

    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    private ConclusionStatus status;

    // № заключения
    private String number;

    //Дата заключения
    @Column(name="date", columnDefinition = "timestamp without time zone")
    private Date date;

    //Дата окончания срока
    @Column(name = "deadline_date")
    private Date deadlineDate;

    // text conslusion
    @Column(columnDefinition = "TEXT")
    private String htmlText;

    /*@OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_repo_id", insertable = false, updatable = false)
    private DocumentRepo documentRepo;*/

    @Column(name = "document_repo_id")
    private Integer documentRepoId;

    @Column(name = "conclusion_file_id")
    private Integer conclusionFileId; // for reestrga yuklangan arizalar uchun xulosa yuklash

    @Column(name = "file_id")
    private Integer fileId;

    /*
     * Technical Fields
     */

    //ilova fayllar
    @ManyToMany
    @JoinTable(name = "conclusion_jt_files",
            joinColumns = { @JoinColumn(name = "files") },
            inverseJoinColumns = { @JoinColumn(name = "file_id") })
    private Set<File> files;

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
