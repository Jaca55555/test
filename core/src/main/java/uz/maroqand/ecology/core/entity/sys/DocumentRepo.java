package uz.maroqand.ecology.core.entity.sys;

import lombok.Data;
import uz.maroqand.ecology.core.constant.sys.DocumentRepoType;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Utkirbek Boltaev on 07.10.2019.
 * (uz) document repository
 * (ru)
 */
@Data
@Entity
@Table(name = "document_repo")
public class DocumentRepo {

    @Transient
    private static final String sequenceName = "document_repo_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;

    @Column(name = "type")
    @Enumerated(EnumType.ORDINAL)
    private DocumentRepoType type;

    @Column(name = "application_id")
    private Integer applicationId;

    private String uuid;

    private String code;

    @Column(name="created_at", columnDefinition = "timestamp without time zone")
    private Date createdAt;
    
}