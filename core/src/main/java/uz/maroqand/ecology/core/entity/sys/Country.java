package uz.maroqand.ecology.core.entity.sys;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by Utkirbek Boltaev on 20.05.2019.
 */
@Data
@Entity
@Table(name = "sys_country")
public class Country {

    @Transient
    private static final String sequenceName = "sys_country_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;

    @Column(name = "name_short")
    private String shortName;

    @Column(name = "name_full")
    private String fullName;

    @Column(name = "name_short_uz")
    private String shortNameUz;

    @Column(name = "name_full_uz")
    private String fullNameUz;

    @Column(name = "name_short_oz")
    private String shortNameOz;

    @Column(name = "name_full_oz")
    private String fullNameOz;

    @Column(name = "name_short_en")
    private String shortNameEn;

    @Column(name = "name_full_en")
    private String fullNameEn;

    @Column(name = "code_alpha_2")
    private String alphaCode2;

    @Column(name = "code_alpha_3")
    private String alphaCode3;

    //(uz) status = TRUE bo'lsa ma'lumotdan foydalanisla bo'ladi(active), status = FALSE bo'lsa ma'lumotdan foydalanish yopilgan(Closed)
    //(ru) status = TRUE данные могут быть использованы(active), status = FALSE данные закрываются(Closed)
    @Column(name = "status", columnDefinition = "boolean DEFAULT true")
    private Boolean status = true;

    public String getNameTranslation(String locale) {
        switch (locale) {
            case "en":
                return fullNameEn;
            case "uz":
                return fullNameUz;
            case "oz":
                return fullNameOz;
            default:
                return fullName;
        }
    }

}
