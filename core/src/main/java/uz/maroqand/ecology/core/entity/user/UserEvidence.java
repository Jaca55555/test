package uz.maroqand.ecology.core.entity.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import uz.maroqand.ecology.core.entity.sys.File;
import uz.maroqand.ecology.core.entity.sys.TableHistory;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Data
@Entity
@Table(name = "sys_user_evidence")
public class UserEvidence {

    @Transient
    private static final String sequenceName = "sys_user_evidence_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",updatable = false,insertable = false)
    private User user;

    @Column(name = "user_id")
    private Integer userId;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "table_history_id",updatable = false,insertable = false)
    private TableHistory tableHistory;

    @Column(name = "table_history_id")
    private Long tableHistoryId;

    //Sabab
    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    //Yozilgan sana
    @Column(name="registered_date", columnDefinition = "timestamp without time zone")
    private Date registeredDate;

    //ilova fayllar
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "sys_user_evidence_jt_document_files",
            joinColumns = { @JoinColumn(name = "sys_user_evidence") },
            inverseJoinColumns = { @JoinColumn(name = "file_id") })
    private Set<File> documentFiles;

    @Column(name = "evidince_status")
    @Enumerated(EnumType.ORDINAL)
    private EvidinceStatus evidinceStatus;

    /*Deleted,
    Create,
    Active,
    NotActive*/
}
