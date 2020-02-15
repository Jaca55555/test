package uz.maroqand.ecology.docmanagement.entity;

import lombok.Data;
import uz.maroqand.ecology.core.entity.user.User;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Namazov Jamshid
 * email: <jamwid07@mail.ru>
 * date: 11.02.2020
 */
@Data
@Entity
@Table(name = "document_task_participant")
public class DocumentTaskSub {

    @Transient
    private static final String sequenceName = "document_task_participant_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", insertable = false, updatable = false)
    private Document document;

    @Column(name = "document_id")
    private Integer documentId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_task_id", insertable = false, updatable = false)
    private DocumentTask task;

    @Column(name = "document_task_id")
    private Integer taskId;

    //topshirish
    @Column(name = "content", columnDefinition = "text")
    private String content;

    //topshiriq muddati
    @Column(name = "due_date", columnDefinition = "timestamp without time zone")
    private Date dueDate;

    //TaskSubType
    @Column(name = "type")
    private Integer type;

    //TaskSubStatus
    @Column(name = "status")
    private Integer status;


    //yuborgan user, User.id
    @Column(name = "sender_id")
    private Integer senderId;

    //qabul qiluvshi
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", insertable = false, updatable = false)
    private User receiver;

    @Column(name = "receiver_id")
    private Integer receiverId;

    //User.departmentId (receiver)
    @Column(name = "department_id")
    private Integer departmentId;

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