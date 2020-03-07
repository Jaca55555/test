package uz.maroqand.ecology.core.entity.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import uz.maroqand.ecology.core.constant.user.Permissions;
import uz.maroqand.ecology.core.entity.sys.Organization;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

/**
 * Created by Utkirbek Boltaev on 10.06.2019.
 * (uz)
 * (ru)
 */
@Data
@Entity
@Table(name = "sys_role")
public class Role {

    @Transient
    private static final String sequenceName = "sys_role_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;

    //Organization.ID
    @Column(name = "organization_id")
    private Integer organizationId;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", insertable = false, updatable = false)
    private Organization organization;

    private String name;

    private String description;

    @ElementCollection(targetClass = Permissions.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "sys_role_jt_permissions",
            joinColumns = @JoinColumn(name = "role_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "permission_id")
    private Set<Permissions> permissions;

    /*
     * Technical Fields
     */
    @Column(name = "deleted",columnDefinition = "boolean DEFAULT false")
    private Boolean deleted = false;

    // (uz) qo'shimcha ma'lumot
    // (ru) больше информации
    private String message;

    @Column(name="created_at", columnDefinition = "timestamp without time zone")
    private Date createdAt;

    @Column(name="updated_at", columnDefinition = "timestamp without time zone")
    private Date updatedAt;

    @Column(name = "created_by_id")
    private Integer createdById;

    @Column(name = "updated_by_id")
    private Integer updatedById;

}
