package uz.maroqand.ecology.core.entity.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import uz.maroqand.ecology.core.entity.sys.Organization;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Utkirbek Boltaev on 15.06.2019.
 * (uz)
 * (ru)
 */
@Data
@Entity
@Table(name = "sys_position")
public class Position {

    @Transient
    private static final String sequenceName = "sys_position_id_seq";

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

    @Column(name = "name_ru")
    private String nameRu;

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

    private Integer lastDocNumber;

    private String docIndex;

    public String getNameTranslation(String locale) {
        switch (locale) {
            case "ru":
                return nameRu;

            default:
                return name;
        }
    }

}
