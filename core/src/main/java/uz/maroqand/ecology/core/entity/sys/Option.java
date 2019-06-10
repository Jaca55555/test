package uz.maroqand.ecology.core.entity.sys;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by Utkirbek Boltaev on 10.06.2019.
 * (uz)
 * (ru)
 */
@Data
@Entity
@Table(name = "sys_options")
public class Option {

    @Transient
    static final String sequenceName = "sys_options_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "value", columnDefinition = "TEXT")
    private String value;

}
