package uz.maroqand.ecology.core.entity.expertise;

import lombok.Data;

import javax.persistence.*;

//Ҳаво бассейнини атмосфера хавосини ифлослантирувчи манбалар ҳақида маьлумот
@Data
@Entity
@Table(name = "air_pool")
public class AirPool {

    @Transient
    private static final String sequenceName = "air_pool_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;


    //    Ишлабчиқариш номи
    @Column
    private String name;

    //    Цех, участка ва бошқа ишўринлари номи
    @Column(name = "job_name")
    private String jobName;

    //    Зарарли ифлослантирувчи моддалар и/ч манбаноми, ишлабчикариш куввати, ёкилги тури
    @Column(name = "fuel_type")
    private String fuelType;

    //    Зарарли моддалар и/ч манбалар сони
    @Column(name = "number_of_sources")
    private Integer numberOfSources;

    //    Ифлослантирувчи моддалар хосил булувчи жараен
    @Column(name = "substances")
    private String substances;

    //    Ифлослантирувчи моддаларнинг атмосфера хавосига чиқарилиши
    @Column(name = "air_substance")
    private String airSubstance;

    @Column(name="deleted", columnDefinition = "boolean default false")
    private Boolean deleted;

}
