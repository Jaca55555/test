package uz.maroqand.ecology.core.entity.expertise;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import uz.maroqand.ecology.core.constant.expertise.Category;

import javax.persistence.*;

/**
 * Created by Utkirbek Boltaev on 14.06.2019.
 * (uz) Давлат экологик экспертизаси амалга ошириладиган фаолият турлари
 * (ru) видов деятельности, по которым осуществляется государственная экологическая экспертиза
 */
@Data
@Entity
@Table(name = "activity")
@JsonIgnoreProperties
public class Activity {

    @Transient
    private static final String sequenceName = "activity_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;

    @Column(name = "category")
    @Enumerated(EnumType.ORDINAL)
    private Category category;

    private String name;

    @Column(name = "name_oz")
    private String nameOz;

    @Column(name = "name_en")
    private String nameEn;

    @Column(name = "name_ru")
    private String nameRu;

    @Column(name = "enabled", columnDefinition = "boolean DEFAULT false")
    private Boolean enabled;

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
