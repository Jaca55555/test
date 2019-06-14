package uz.maroqand.ecology.core.entity.expertise;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by Utkirbek Boltaev on 14.06.2019.
 * (uz) Давлат экологик экспертизасининг объектлари
 * (ru) Объект экспертизы
 */
@Data
@Entity
@Table(name = "object_expertise")
public class ObjectExpertise {

    @Transient
    private static final String sequenceName = "object_expertise_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;

    private String name;

    @Column(name = "name_ru")
    private String nameRu;

}
