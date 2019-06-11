package uz.maroqand.ecology.core.entity.sys;

import lombok.Data;
import uz.maroqand.ecology.core.constant.sys.AppealStatus;
import uz.maroqand.ecology.core.constant.sys.AppealType;
import uz.maroqand.ecology.core.entity.user.User;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Utkirbek Boltaev on 11.06.2019.
 * (uz)
 */
@Data
@Entity
@Table(name = "sys_appeal")
public class Appeal {

    @Transient
    static final String sequenceName = "sys_appeal_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;

    @Column(name = "type")
    @Enumerated(EnumType.ORDINAL)
    private AppealType appealType;

    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    private AppealStatus appealStatus;

    private String title;

    private String phone;

    @Column(name = "description",columnDefinition = "TEXT")
    private String description;

    @Column(name = "show_user_comment_count")
    private Integer showUserCommentCount;

    @Column(name = "show_admin_comment_count")
    private Integer showAdminCommentCount;

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

    /*@OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "update_by", insertable = false, updatable = false)
    private User updateBy;*/

    @Column(name = "update_by")
    private Integer updateById;

}
