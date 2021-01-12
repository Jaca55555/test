package uz.maroqand.ecology.core.entity.expertise;

import lombok.Data;
import uz.maroqand.ecology.core.constant.expertise.CommentType;
import uz.maroqand.ecology.core.entity.sys.File;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.Set;

/**
 * Created by Sadullayev Akmal on 26.11.2020.
 * (uz)
 * (ru)
 */
@Data
@Entity
@Table(name = "pollution_measures")
public class PollutionMeasures {

    @Transient
    private static final String sequenceName = "pollution_measures_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;


    @Size(max = 500)
    @Column(name = "event_name" , columnDefinition = "TEXT")
    private String eventName;


    /*
     * Technical Fields
     */
    @Column(name = "deleted", columnDefinition = "boolean DEFAULT false")
    private Boolean deleted = false;
}
