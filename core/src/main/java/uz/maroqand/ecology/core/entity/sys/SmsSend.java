package uz.maroqand.ecology.core.entity.sys;

import lombok.Data;
import uz.maroqand.ecology.core.constant.sys.SmsSendStatus;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "sys_sms_send")
public class SmsSend {

    @Transient
    private static final String sequenceName = "sys_sms_send_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "phone")
    private String phone;

    @Column(name = "message")
    private String message;

    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    private SmsSendStatus status;

    @Column(name = "sent_id")
    private Integer sentId;

    @Column(name = "deleted", columnDefinition = "boolean DEFAULT false")
    private Boolean deleted = false;

    @Column(name = "created_at", columnDefinition = "timestamp without time zone")
    private Date createdAt;

    @Column(name = "sent_at", columnDefinition = "timestamp without time zone")
    private Date sentAt;

    @Column(name = "update_at", columnDefinition = "timestamp without time zone")
    private Date updateAt;

}
