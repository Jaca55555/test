package uz.maroqand.ecology.core.entity.expertise;

import lombok.Data;
import uz.maroqand.ecology.core.entity.sys.File;
import uz.maroqand.ecology.core.entity.user.User;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.Set;

/**
 * Created by Utkirbek Boltaev on 16.06.2019.
 * (uz)
 * (ru)
 */
@Data
@Entity
@Table(name = "comment")
public class Comment {

    @Transient
    private static final String sequenceName = "comment_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;

    /*@OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reg_application_id", insertable = false, updatable = false)
    private RegApplication regApplication;*/

    @Column(name = "reg_application_id")
    private Integer regApplicationId;

    @Size(max = 500)
    @Column(columnDefinition = "TEXT")
    private String message;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "comment_jt_document_files",
            joinColumns = { @JoinColumn(name = "comment_id") },
            inverseJoinColumns = { @JoinColumn(name = "file_id") })
    private Set<File> documentFiles;

    /*
     * Technical Fields
     */
    @Column(name = "deleted", columnDefinition = "boolean DEFAULT false")
    private Boolean deleted = false;

    /*@OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by",insertable = false,updatable = false)
    private User createdBy;*/

    @Column(name = "created_by")
    private Integer createdById;

    @Column(name = "created_at", columnDefinition = "timestamp without time zone")
    private Date createdAt;

}
