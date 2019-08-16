package uz.maroqand.ecology.core.entity.expertise;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Created by Utkirbek Boltaev on 14.06.2019.
 * (uz)
 * (ru)
 */
@Getter @Setter
@Entity
@Table(name = "material")
public class Material {

    @Transient
    private static final String sequenceName = "material_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;

    private String name;

    @Column(name = "name_oz")
    private String nameOz;

    @Column(name = "name_en")
    private String nameEn;

    @Column(name = "name_ru")
    private String nameRu;

    @Column(name = "name_short")
    private String nameShort;

    @Column(name = "name_short_oz")
    private String nameShortOz;

    @Column(name = "name_short_en")
    private String nameShortEn;

    @Column(name = "name_short_ru")
    private String nameShortRu;

    public String getNameTranslation(String locale) {
        switch (locale) {
            case "uz":
                return name;
            case "oz":
                return nameOz;
            case "en":
                return nameEn;
            case "ru":
            default:
                return nameRu;
        }
    }

    public String getNameShortTranslation(String locale) {
        switch (locale) {
            case "uz":
                return nameShort;
            case "oz":
                return nameShortOz;
            case "en":
                return nameShortEn;
            case "ru":
            default:
                return nameShortRu;
        }
    }

}
