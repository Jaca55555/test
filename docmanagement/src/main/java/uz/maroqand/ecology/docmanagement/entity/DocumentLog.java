package uz.maroqand.ecology.docmanagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import uz.maroqand.ecology.core.entity.sys.File;
import uz.maroqand.ecology.core.entity.user.User;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

/**
 * Created by Namazov Jamshid
 * email: <jamwid07@mail.ru>
 * date: 14.02.2020
 */

@Data
@Entity
@Table
public class DocumentLog
{
    @Transient
    private static final String sequenceName = "document_log_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;

    @Column(name = "content", columnDefinition = "text")
    private String content;

    // log turlarini qanday ko'rsatish kerak?
    @Column(name = "type")
    private Integer type;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "document_log_jt_files",
            joinColumns = { @JoinColumn(name = "log_id") },
            inverseJoinColumns = { @JoinColumn(name = "file_id") })
    private Set<File> contentFiles;

    @Column(name = "document_id")
    private Integer documentId;

    @ManyToOne
    @JoinColumn(name = "document_id", insertable = false, updatable = false)
    private Document document;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", insertable = false, updatable = false)
    private User createdBy;

    @Column(name = "created_by_id")
    private Integer createdById;

    @Column(name="created_at", columnDefinition = "timestamp without time zone")
    private Date createdAt;

}
