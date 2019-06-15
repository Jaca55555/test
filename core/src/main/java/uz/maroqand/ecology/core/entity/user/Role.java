package uz.maroqand.ecology.core.entity.user;

import lombok.Data;
import uz.maroqand.ecology.core.constant.user.Permissions;

import javax.persistence.*;
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

    private String name;

    private String description;

    @ElementCollection(targetClass = Permissions.class,fetch = FetchType.EAGER)
    @CollectionTable(name = "sys_role_jt_permissions",
            joinColumns = @JoinColumn(name = "role_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "permission_id")
    private Set<Permissions> permissions;

}
