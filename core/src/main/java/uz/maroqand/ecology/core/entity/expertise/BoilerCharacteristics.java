package uz.maroqand.ecology.core.entity.expertise;

import lombok.Data;
import uz.maroqand.ecology.core.constant.expertise.LogStatus;
import uz.maroqand.ecology.core.constant.expertise.SubstanceType;

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

    @Column(name = "boiler_type")
    @Enumerated(EnumType.ORDINAL)
    private BoilerCharacteristicsEnum boilerType;

    //qiymati
    @Column(name = "amount", precision = 20, scale = 2)
    private Double amount;

    //qiymati
    @Column(name = "type_boiler" )
    private Integer substanceType;

    @Column(name="deleted", columnDefinition = "boolean default false")
    private Boolean deleted;

}
