package uz.maroqand.ecology.docmanagement.entity;

import lombok.Data;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.docmanagement.constant.TaskStatus;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Namazov Jamshid
 * email: <jamwid07@mail.ru>
 * date: 11.02.2020
 */

@Data
@Entity
@Table(name = "document_task")
public class DocumentTask {

    @Transient
    private static final String sequenceName = "document_task_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", insertable = false, updatable = false)
    private Document document;

    @Column(name = "document_id")
    private Integer documentId;

    //TaskStatus
    @Column(name = "status")
    private Integer status;

    //topshirish
    @Column(name = "content", columnDefinition = "text")
    private String content;

    //topshiriq muddati
    @Column(name = "due_date", columnDefinition = "timestamp without time zone")
    private Date dueDate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chief_id", insertable = false, updatable = false)
    private User chief;

    //topshiriq bergan rahbar
    @Column(name = "chief_id")
    private Integer chiefId;

    //ijrochi
    @Column(name = "performer_id")
    private Integer performerId;

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

    public String getStatusName(Integer id){
        if (id==null) return "";
        TaskStatus taskStatus = TaskStatus.getTaskStatus(id);
        return taskStatus!=null?taskStatus.getName():"";
    }
}
