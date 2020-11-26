package uz.maroqand.ecology.core.entity.expertise;

import lombok.Data;
import uz.maroqand.ecology.core.entity.sys.File;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

/**
 * Created by Sadullayev Akmal on 26.11.2020.
 * (uz)
 * (ru)
 */
@Data
@Entity
@Table(name = "reg_application_category_four_additional")
public class RegApplicationCategoryFourAdditional {

    @Transient
    private static final String sequenceName = "reg_application_category_four_additional_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reg_application_id", insertable = false, updatable = false)
    private RegApplication regApplication;

    @Column(name = "reg_application_id")
    private Integer regApplicationId;


    //step 2

    //Умумий қисм
    @Column(name = "object_blanket" , columnDefinition = "TEXT")
    private String objectBlanket;

    //Танланган ёки ажратилган ер майдони ﻿ҳақида қисқача маълумот
    @Column(name = "coordinate_description" , columnDefinition = "TEXT")
    private String coordinateDescription;

    //Энг яқин аҳоли хонадонлари жойлашган жойлар (чегарадош объектлар тўғрисида маълумот)
    @Column(name = "bordering_objects", columnDefinition = "TEXT")
    private String borderingObjects;

    //Объектнинг муҳофаза қилинадиган ҳудудларга нисбатан жойлашганлиги ﻿тўғрисида маълумот
    @Column(name = "territory_description", columnDefinition = "TEXT")
    private String territoryDescription;

    //Маданий мерос объекларига нисбатан ﻿жойлашганлиги тўғрисида маълумот
    @Column(name = "cultural_heritage_description" , columnDefinition = "TEXT")
    private String culturalHeritageDescription;

    //Танланган ер участкасидаги мавжуд усимлик дунёси (номи, сони, қурилиш остига тушувчи дарахтлар сони)
    @Column(name = "tree_count_additional" , columnDefinition = "TEXT")
    private String treeCountAdditional;

    //Ер ости ва ер усти сувлари тўғрисида маълумот (гидрологик, гидрогеологик, геологик шароитлар)
    @Column(name = "water_information" , columnDefinition = "TEXT")
    private String waterInformation;

    //Мавжуд ва режалаштирилаётган мухандислик иншоотлари тугрисида маьлумот
    @Column(name = "structures_information" , columnDefinition = "TEXT")
    private String structuresInformation;

    //Энг катта шамол йўналиши, йил давомида иситиладиган давр хамда ўртача шамол тезлиги тўғрисида маълумот
    @Column(name = "about_wind_speed" , columnDefinition = "TEXT")
    private String aboutWindSpeed;

    //step 3

    // Умумий майдон
    @Column(name = "common_area", precision = 20, scale = 2)
    private Double commonArea;

    // Бино-иншоотлар остидаги майдон
    @Column(name = "building_under_area", precision = 20, scale = 2)
    private Double buildingUnderArea;

    // Кўкаламзорлаштирилган майдон
    @Column(name = "area_additional", precision = 20, scale = 2)
    private Double areaAdditional;

    // Асфальт йўл ва йўлаклар ётказилган майдони
    @Column(name = "road_area", precision = 20, scale = 2)
    private Double RoadArea;

    //Объектнинг иш фаолияти, иш тартиби,ишчилар сони
    @Column(name = "work_schedule", columnDefinition = "TEXT")
    private String workSchedule;

    //Объектнинг ишлаб чиқариш куввати
    @Column(name = "production_capacity", columnDefinition = "TEXT")
    private String productionCapacity;

    //Технологик жараён тавсифи
    @Column(name = "technological_process", columnDefinition = "TEXT")
    private String technologicalProcess;

    //Хомашё тавсифи ва миқдори
    @Column(name = "material_additional", columnDefinition = "TEXT")
    private String materialAdditional;

    //Хомашё манбалари
    @Column(name = "source_material", columnDefinition = "TEXT")
    private String sourceMaterial;




    //step 4




    //step 7

    //Худуддан фойдаланиш турлари кўрсатилган ҳолда объектни жойлаштириш режаси
    @Column(name = "object_placement_plan", columnDefinition = "TEXT")
    private String objectPlacementPlan;

    //Ишлаб чиқариш технологиясининг тавсифи
    @Column(name = "production_description", columnDefinition = "TEXT")
    private String productionDescription;

    //Канализация мавжудлиги тўғрисидаги маълумотлар ва оқова сувларни оқизишга нисбатан қўйиладиган талаблар
    @Column(name = "sewage_availability", columnDefinition = "TEXT")
    private String sewageAvailability;





    //Дата заключения
    @Column(name="date", columnDefinition = "timestamp without time zone")
    private Date date;

    //Дата окончания срока
    @Column(name = "deadline_date")
    private Date deadlineDate;

    // text conslusion
    @Column(columnDefinition = "TEXT")
    private String htmlText;

    /*@OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_repo_id", insertable = false, updatable = false)
    private DocumentRepo documentRepo;*/

    @Column(name = "document_repo_id")
    private Integer documentRepoId;

    @Column(name = "conclusion_word_file_id")
    private Integer conclusionWordFileId; // for online yozilgan file id

    @Column(name = "conclusion_file_id")
    private Integer conclusionFileId; // for reestrga yuklangan arizalar uchun xulosa yuklash

    @Column(name = "uploaded_user")
    private Integer uploadedUser; // for reestrga yuklangan arizalar uchun xulosa yuklasgan userId

    /*
     * Technical Fields
     */

    //ilova fayllar
    @ManyToMany
    @JoinTable(name = "conclusion_jt_files",
            joinColumns = { @JoinColumn(name = "files") },
            inverseJoinColumns = { @JoinColumn(name = "file_id") })
    private Set<File> files;

    @Column(name = "deleted",columnDefinition = "boolean DEFAULT false")
    private Boolean deleted = false;

    @Column(name="created_at", columnDefinition = "timestamp without time zone")
    private Date createdAt;

    @Column(name="update_at", columnDefinition = "timestamp without time zone")
    private Date updateAt;

    /*@OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", insertable = false, updatable = false)
    private User createdBy;*/

    @Column(name = "created_by")
    private Integer createdById;

    /*@OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "update_by", insertable = false, updatable = false)
    private User updateBy;*/

    @Column(name = "update_by")
    private Integer updateById;

}
