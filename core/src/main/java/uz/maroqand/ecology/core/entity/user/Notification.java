package uz.maroqand.ecology.core.entity.user;

import lombok.Data;
import uz.maroqand.ecology.core.constant.user.NotificationStatus;
import uz.maroqand.ecology.core.constant.user.NotificationType;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Utkirbek Boltaev on 24.06.2019.
 * (uz)
 * (ru)
 */
@Data
@Entity
@Table(name = "sys_notification")
public class Notification {

    @Transient
    private static final String sequenceName = "sys_notification_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;

    @Column(name = "type")
    @Enumerated(EnumType.ORDINAL)
    private NotificationType type;

    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    private NotificationStatus status;

    @Column
    private String title;

    @Column
    private String message;

    @Column
    private Integer applicationNumber;

    @Column//for doc menegment
    private String registrationNumber;

    /*@OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id", insertable = false, updatable = false)
    private User reviewer;*/

    @Column(name = "reviewer_id")
    private Integer reviewerId;

    @Column(name = "tin")
    private Integer tin;

    @Column
    private String url;

    /*
     * Technical Fields
     */
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

}
