package uz.maroqand.ecology.core.entity.expertise;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by Utkirbek Boltaev on 10.06.2019.
 * (uz)
 */
@Data
@Entity
@Table(name = "opf")
public class Opf {

    @Transient
    private static final String sequenceName = "opf_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "name_ru")
    private String nameRu;

    private Integer code;

    @Column(name = "name_tag")
    private String nameTag;

    @Column(name = "name_short_tag")
    private String nameShortTag;

}
