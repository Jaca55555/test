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

    @ManyToOne
    @JoinColumn(name = "document_id", insertable = false, updatable = false)
    private Document document;

    @Column(name = "document_id")
    private Integer documentId;

    @ManyToOne
    @JoinColumn(name = "task_sub_id", insertable = false, updatable = false)
    private DocumentTaskSub taskSub;

    @Column(name = "task_sub_id")
    private Integer taskSubId;

    @ManyToOne
    @JoinColumn(name = "task_sub", insertable = false, updatable = false)
    private DocumentTask task;

    @Column(name = "task_id")
    private Integer taskId;

    @Column(name = "before_status")
    private String beforeStatus;

    @Column(name = "before_status_color")
    private String beforeStatusColor;

    @Column(name = "after_status")
    private String afterStatus;

    @Column(name = "after_status_color")
    private String afterStatusColor;

    @Column(name = "attached_doc_id")
    private Integer attachedDocId;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne
    @JoinColumn(name = "attached_doc_id", insertable = false, updatable = false)
    private Document attachedDoc;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", insertable = false, updatable = false)
    private User createdBy;

    @Column(name = "created_by_id")
    private Integer createdById;

    @Column(name="created_at", columnDefinition = "timestamp without time zone")
    private Date createdAt;

}
