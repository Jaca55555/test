package uz.maroqand.ecology.core.entity.expertise;

import lombok.Getter;
import lombok.Setter;
import uz.maroqand.ecology.core.constant.expertise.SubstanceType;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "substance")
public class Substance {
    @Transient
    private static final String sequenceName = "substance_id_seq";


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

    @Column(name = "type")
    private SubstanceType type;

    @Column(name="deleted", columnDefinition = "boolean default false")
    private Boolean deleted;

    @Column(name="update_at")
    private Date updateAt;

    @Column(name="update_by")
    private Integer updateBy;

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
}
