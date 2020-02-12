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
public class DocumentTaskParticipant {
    @Transient
    private static final String sequenceName = "document_task_participant_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "document_task_id")
    private DocumentTask task;

    @Column(name = "status")
    private Integer status;

    @Column(name="created_at", columnDefinition = "timestamp without time zone")
    private Date createdAt;
}
