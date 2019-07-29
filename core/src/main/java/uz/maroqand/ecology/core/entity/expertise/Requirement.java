package uz.maroqand.ecology.core.entity.expertise;

import lombok.Data;
import uz.maroqand.ecology.core.constant.expertise.Category;

import javax.persistence.*;
import java.util.Set;

/**
 * Created by Utkirbek Boltaev on 15.06.2019.
 * (uz)
 * (ru)
 */
@Data
@Entity
@Table(name = "expertise_requirement")
public class Requirement {

    @Transient
    private static final String sequenceName = "expertise_requirement_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;

    //ekspertiza obyekti
    /*@OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "object_expertise_id", insertable = false, updatable = false)
    private ObjectExpertise objectExpertise;*/

    @Column(name = "object_expertise_id")
    private Integer objectExpertiseId;

    //ekspertiza obyekti kategoriyasi
    @Enumerated
    @Column(name = "category")
    private Category category;

    //markaz ko'rib chiqadi
    @Column(name = "deleted", columnDefinition = "boolean DEFAULT false")
    private Boolean reviewMainOrganization = false;

    //miqdori (MRZP da bo'lishi, summada bo'lishi mumkin)
    @Column(name = "qty", precision = 20, scale = 2)
    private Double qty;

    // muddati (kun)
    private Integer deadline;

    @Column(name = "material_name")
    private String materialName;

    @ElementCollection(targetClass = Integer.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "expertise_requirement_jt_material",
            joinColumns = @JoinColumn(name = "expertise_requirement_id"))
    @Column(name = "material_id")
    private Set<Integer> materials;

    //ekspertizadan o'tkazuvchi organ
    /*@OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", insertable = false, updatable = false)
    private Organization review;*/

    @Column(name = "review_id")
    private Integer reviewId = 1;

    //ekspertizaga taqdim etiladigan materiallar
    /*@OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", insertable = false, updatable = false)
    private Material material;*/

    /*@Column(name = "material_id")
    private Integer materialId;*/

}
