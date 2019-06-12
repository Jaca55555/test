package uz.maroqand.ecology.core.entity.sys;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by Utkirbek Boltaev on 10.06.2019.
 * (uz)
 */
@Data
@Entity
@Table(name = "sys_opf")
public class Opf {

    @Transient
    private static final String sequenceName = "sys_opf_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;

    private Integer code;

    @Column(name = "parent_id")
    private String parentId;

    @Column(name = "name")
    private String name;

    @Column(name = "name_ru")
    private String nameRu;

    @Column(name = "name_short")
    private String nameShort;

    @Column(name = "name_short_ru")
    private String nameShortRu;

    public String getNameTranslation(String locale) {
        switch (locale) {
            case "uz":
                return name;
            case "ru":
            default:
                return nameRu;
        }
    }

    public String getNameShortTranslation(String locale) {
        switch (locale) {
            case "uz":
                return nameShort;
            case "ru":
            default:
                return nameShortRu;
        }
    }

}
