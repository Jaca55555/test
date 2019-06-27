package uz.maroqand.ecology.core.entity.user;

import lombok.Data;

import javax.persistence.*;

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

    private String name;

    @Column(name = "name_ru")
    private String nameRu;

    @Column(name = "deleted",columnDefinition = "boolean DEFAULT false")
    private Boolean deleted = false;

    public String getNameTranslation(String locale) {
        switch (locale) {
            case "ru":
                return nameRu;

            default:
                return name;
        }
    }

}
