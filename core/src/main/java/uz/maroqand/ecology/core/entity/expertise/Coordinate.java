package uz.maroqand.ecology.core.entity.expertise;

import lombok.Data;
import uz.maroqand.ecology.core.entity.client.Client;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Utkirbek Boltaev on 08.07.2019.
 * (uz)
 * (ru)
 */
@Data
@Entity
@Table(name = "coordinate")
public class Coordinate {

    @Transient
    private static final String sequenceName = "coordinate_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;

    /*@OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reg_application_id", updatable = false, insertable = false)
    private RegApplication regApplication;*/

    @Column(name = "reg_application_id")
    private Integer regApplicationId;

    /*@OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", updatable = false, insertable = false)
    private Client client;*/

    @Column(name = "client_id")
    private Integer clientId;


    //Object name
    @Column
    private String clientName;



    //Country
    /*@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", insertable = false, updatable = false)
    private Country country;*/

    @Column(name = "country_id")
    private Integer countryId;

    //Регион
    /*@OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", updatable = false, insertable = false)
    private Soato region;*/

    @Column(name = "region_id")
    private Integer regionId;

    //Район
    /*@OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_region_id", updatable = false, insertable = false)
    private Soato subRegion;*/

    @Column(name = "sub_region_id")
    private Integer subRegionId;

    //Object name
    @Column
    private String name;

    private String latitude;

    private String longitude;

    /*
     * Technical Fields
     */
    @Column(name = "deleted",columnDefinition = "boolean DEFAULT false")
    private Boolean deleted = false;

    @Column(name="created_at", columnDefinition = "timestamp without time zone")
    private Date createdAt;

    @Column(name="updated_at", columnDefinition = "timestamp without time zone")
    private Date updatedAt;

    @Column(name = "created_by_id")
    private Integer createdById;

    @Column(name = "updated_by_id")
    private Integer updatedById;

}
