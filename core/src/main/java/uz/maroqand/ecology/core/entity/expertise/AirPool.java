package uz.maroqand.ecology.core.entity.expertise;

import lombok.Data;
import uz.maroqand.ecology.core.entity.sys.File;

import javax.persistence.*;
import java.util.Set;

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

//    4.2
    @ManyToMany
    @JoinTable(name = "air_pool_jt_description_of_sources",
            joinColumns = { @JoinColumn(name = "air_pool") },
            inverseJoinColumns = { @JoinColumn(name = "description_of_sources")})
    @OrderBy(value = "id asc")
    private Set<DescriptionOfSources> descriptionOfSources;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "description_of_sources_jt_files",
            joinColumns = { @JoinColumn(name = "description_of_sources_id") },
            inverseJoinColumns = { @JoinColumn(name = "file_id") })
    private Set<File> files;


    @ManyToMany
    @JoinTable(name = "description_of_sources_jt_additional",
            joinColumns = { @JoinColumn(name = "description_of_sources_id") },
            inverseJoinColumns = { @JoinColumn(name = "additional_id")})
    @OrderBy(value = "id asc")
    private Set<DescriptionOfSourcesAdditional> descriptionOfSourcesAdditionals;

    @Column(name="deleted", columnDefinition = "boolean default false")
    private Boolean deleted;

}
