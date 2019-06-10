package uz.maroqand.ecology.core.entity.sys;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by Utkirbek Boltaev on 10.06.2019.
 * (uz)
 */
@Data
@Entity
@Table(name = "sys_translation")
public class Translation {

    @Transient
    private static final String sequenceName = "sys_translation_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;

    @Column
    private String name;

    @Column(columnDefinition = "TEXT")
    private String uzbek;

    @Column(columnDefinition = "TEXT")
    private String ozbek;

    @Column(columnDefinition = "TEXT")
    private String russian;

    @Column(columnDefinition = "TEXT")
    private String english;

    @Column(name = "is_html")
    private Boolean isHtml = false;

    public String getNameTranslation(String locale) {
        switch (locale) {
            case "en":
                return english;

            case "uz":
                return uzbek;

            case "oz":
                return ozbek;

            case "ru":
            default:
                return russian;
        }
    }

}
