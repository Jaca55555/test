package uz.maroqand.ecology.core.entity.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import uz.maroqand.ecology.core.constant.user.LoginType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Utkirbek Boltaev on 10.06.2019.
 * (uz)
 */
@Data
@Entity
@Table(name = "sys_user_additional")
public class UserAdditional {

    @Transient
    private static final String sequenceName = "sys_user_additional_id_seq";

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

    @Column(name = "gui_user_id")
    private Integer guiUserId;

    @Column(name="registered_date", columnDefinition = "timestamp without time zone")
    private Date registeredDate;

    @Column(name = "info", columnDefinition = "TEXT")
    private String info;

    @Column(name = "ip_client")
    private String ipClient;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "login_type")
    @Enumerated(EnumType.ORDINAL)
    private LoginType loginType;

}
