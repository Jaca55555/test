package uz.maroqand.ecology.core.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by Utkirbek Boltaev on 14.06.2019.
 * (uz)
 * (ru)
 */
@Data
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

    @Column(name = "name_ru")
    private String nameRu;

}
