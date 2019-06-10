package uz.maroqand.ecology.core.entity.expertise;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by Utkirbek Boltaev on 10.06.2019.
 * (uz)
 */
@Data
@Entity
@Table(name = "sys_soato")
public class Soato {

    @Transient
    private static final String sequenceName = "sys_soato_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "name_ru")
    private String nameRu;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id",updatable = false,insertable = false)
    private Soato parent;

    @Column(name = "parent_id")
    private Long parentId;

    private Integer level;

    @Column(name = "status", columnDefinition = "boolean DEFAULT true")
    private Boolean status=true;

    public String getNameTranslation(String locale) {
        switch (locale) {
            case "uz":
                return name;
            case "ru":
            default:
                return nameRu;
        }
    }

}
