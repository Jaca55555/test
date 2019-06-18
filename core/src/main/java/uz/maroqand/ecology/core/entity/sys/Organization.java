package uz.maroqand.ecology.core.entity.sys;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Size;

/**
 * Created by Utkirbek Boltaev on 14.06.2019.
 * (uz)
 * (ru)
 */
@Data
@Entity
@Table(name = "organization")
public class Organization {

    @Transient
    private static final String sequenceName = "organization_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;

    @Size(max = 9)
    private Integer tin;

    private String name;

    @Column(name = "name_ru")
    private String nameRu;

    @Column(name = "account")
    private String account;

    @Column(name = "mfo")
    private String mfo;

    //Регион
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", updatable = false, insertable = false)
    private Soato region;

    @Column(name = "region_id")
    private Integer regionId;

    //Район
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_region_id", updatable = false, insertable = false)
    private Soato subRegion;

    @Column(name = "sub_region_id")
    private Integer subRegionId;

    @Column(name = "address")
    private String address;

    @Column(name = "last_number")
    private Integer lastNumber;

    public String getNameTranslation(String locale) {
        switch (locale) {
            case "ru":
                return nameRu;

            default:
                return name;
        }
    }

}
