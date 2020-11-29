package uz.maroqand.ecology.core.entity.expertise;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

//Қозон характеристикаси
@Data
@Entity
@Table(name = "boiler_characteristics")
public class BoilerCharacteristics {

    @Transient
    private static final String sequenceName = "boiler_characteristics_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;


    @Column
    private String name;

    //Ўлчов бирлиги
    @Column
    private String type;

    //qiymati
    @Column(name = "amount", precision = 20, scale = 2)
    private Double amount;

    @Column(name="deleted", columnDefinition = "boolean default false")
    private Boolean deleted;

}
