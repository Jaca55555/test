package uz.maroqand.ecology.core.entity.expertise;

import lombok.Data;
import uz.maroqand.ecology.core.constant.expertise.CommentStatus;
import uz.maroqand.ecology.core.constant.expertise.CommentType;
import uz.maroqand.ecology.core.entity.sys.File;

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

    @Column(name = "reg_application_id")
    private Integer regApplicationId;

    @Column(name = "type")
    @Enumerated(EnumType.ORDINAL)
    private CommentType type;

    @Size(max = 500)
    @Column(columnDefinition = "TEXT")
    private String message;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "comment_jt_document_files",
            joinColumns = { @JoinColumn(name = "comment_id") },
            inverseJoinColumns = { @JoinColumn(name = "file_id") })
    private Set<File> documentFiles;

    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    private CommentStatus status;


    /*
     * Technical Fields
     */
    @Column(name = "deleted", columnDefinition = "boolean DEFAULT false")
    private Boolean deleted = false;

    @Column(name = "created_by")
    private Integer createdById;

    @Column(name = "created_at", columnDefinition = "timestamp without time zone")
    private Date createdAt;

}
