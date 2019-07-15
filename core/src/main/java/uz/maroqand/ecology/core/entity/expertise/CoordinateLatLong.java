package uz.maroqand.ecology.core.entity.expertise;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Utkirbek Boltaev on 08.07.2019.
 * (uz)
 * (ru)
 */
@Data
@Entity
@Table(name = "coordinate_lat_long")
public class CoordinateLatLong {

    @Transient
    private static final String sequenceName = "coordinate_lat_long_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;

    /*@OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coordinate_id", updatable = false, insertable = false)
    private Coordinate coordinate;*/

    @Column(name = "coordinate_id")
    private Integer coordinateId;

    //CoordinateLatLong.ID
    @Column(name = "before_id")
    private Integer beforeId;

    private String latitude;

    private String longitude;

    //CoordinateLatLong.ID
    @Column(name = "after_id")
    private Integer afterId;

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