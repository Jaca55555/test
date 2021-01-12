package uz.maroqand.ecology.core.entity.expertise;

import lombok.Data;
import uz.maroqand.ecology.core.entity.sys.File;

import javax.persistence.*;
import java.util.Set;

//Ҳаво бассейнини атмосфера хавосини ифлослантирувчи манбалар ҳақида маьлумот
@Data
@Entity
@Table(name = "description_of_sources")
public class DescriptionOfSources {

    @Transient
    private static final String sequenceName = "description_of_sources_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;


    //Baladlik
    @Column(name = "sources_height", precision = 20, scale = 2)
    private Double sourcesHeight;

    //diometr
    @Column(name = "sources_diometer", precision = 20, scale = 2)
    private Double sourcesDiometer;

    //
    @Column(name = "sources_w", precision = 20, scale = 2)
    private Double sourcesW;

    //
    @Column(name = "sources_v", precision = 20, scale = 2)
    private Double sourcesV;

    //
    @Column(name = "sources_t", precision = 20, scale = 2)
    private Double sourcesT;

    @Column(name="deleted", columnDefinition = "boolean default false")
    private Boolean deleted;
}
