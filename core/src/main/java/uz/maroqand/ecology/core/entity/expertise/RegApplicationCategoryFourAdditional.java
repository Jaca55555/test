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

    //Объект худудининг фойдаланиш тури
    @Column(name = "object_blanket" , columnDefinition = "TEXT")
    private String objectBlanket;

    /*//Танланган ёки ажратилган ер майдони ﻿ҳақида қисқача маълумот
    @Column(name = "coordinate_description" , columnDefinition = "TEXT")
    private String coordinateDescription;*/

    //Энг якин турар-жой биноларигача бўлган масофа (м)
    @Column(name = "bordering_objects", columnDefinition = "TEXT")
    private String borderingObjects;

    //Ер усти сув оқимларининг мавжудлиги/мавжуд эмаслиги тўғрисида маълумот
    @Column(name = "territory_description", columnDefinition = "TEXT")
    private String territoryDescription;

    //Ер ости сувлари тўғрисида маълумот
    @Column(name = "cultural_heritage_description" , columnDefinition = "TEXT")
    private String culturalHeritageDescription;

    //Муҳофаза этиладиган табиий ҳудудлар тўғрисида маълумот
    @Column(name = "animal_count_additional" , columnDefinition = "TEXT")
    private String animalCountAdditional;

    //Маданий мерос объектлари тўғрисида маълумот
    @Column(name = "tree_count_additional" , columnDefinition = "TEXT")
    private String treeCountAdditional;

    //Лойиҳалаш ишлари олиб борилаётган ҳудудда Ўзбекистон Республикаси
    @Column(name = "water_information" , columnDefinition = "TEXT")
    private String waterInformation;

    //“Қизил китоби”га киритилган ўсимликлар ва ҳайвонларнинг мавжудлиги/мавжуд эмаслиги тўғрисидаги маълумотлар
    @Column(name = "structures_information" , columnDefinition = "TEXT")
    private String structuresInformation;

    //Объектнинг режалаштирилган жойлашуви ҳудудида дарахтларнинг ўсиши ёки мавжуд эмаслиги, шунингдек, кесиладиган дарахтларнинг мавжудлиги/мавжуд эмаслиги тўғрисида маълумот
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
    private Double roadArea;

    //Объектнинг иш фаолияти, иш тартиби,ишчилар сони
    @Column(name = "work_schedule", columnDefinition = "TEXT")
    private String workSchedule;

    //Режалаштирилган фаолият, объектнинг ишлаб чиқариш қуввати  (т/йилига,  дона/йилигав.ҳ)
    @Column(name = "production_capacity", columnDefinition = "TEXT")
    private String productionCapacity;

    //Ишлаб чиқариладиган маҳсулотлари рўйхати
    @Column(name = "technological_process", columnDefinition = "TEXT")
    private String technologicalProcess;

    //Корхонанинг таркиби
    @Column(name = "material_additional", columnDefinition = "TEXT")
    private String materialAdditional;

    //Технологик жиҳозларнинг таркиби
    @Column(name = "step_3_material", columnDefinition = "TEXT")
    private String step3Material;

    //Хом ашё ва материалларнинг таркиби
    @Column(name = "source_material", columnDefinition = "TEXT")
    private String sourceMaterial;

    //Технологик жараённинг тавсифи
    @Column(name = "step_3_texno", columnDefinition = "TEXT")
    private String step3Texno;

    //Объектнинг иссиқлик таъминоти манбаи
    @Column(name = "step_3_heat", columnDefinition = "TEXT")
    private String step3Heat;

    //Объектда сув таъминоти манбаи, сув истеъмолчилари
    @Column(name = "step_3_water", columnDefinition = "TEXT")
    private String step3Water;

    //Канализация ва канализация тозалаш иншоотларининг мавжудлиги
    @Column(name = "step_3_sewer", columnDefinition = "TEXT")
    private String step3Sewer;

    //Оқова сувларни қабул қилувчи
    @Column(name = "step_3_sewage", columnDefinition = "TEXT")
    private String step3Sewage;

    //Сувни қайта ишлаш ёки сувни қайта ишлатиш тизимларининг мавжудлиги
    @Column(name = "step_3_water_treatment", columnDefinition = "TEXT")
    private String step3WaterTreatment;

    //Чиқиндилар рўйхати
    @Column(name = "step_3_waste", columnDefinition = "TEXT")
    private String step3Waste;

    //Режалаштирилган экологик чора-тадбирлар
    @Column(name = "step_3_event", columnDefinition = "TEXT")
    private String step3Event;

    //Маиший чиқиндилар билан боғлиқ ишларни амлага оширилиши тўғрисида маълумот
    @Column(name = "step_3_household", columnDefinition = "TEXT")
    private String step3Household;


    @ManyToMany
    @JoinTable(name = "step3_jt_files",
            joinColumns = { @JoinColumn(name = "step3") },
            inverseJoinColumns = { @JoinColumn(name = "files")})
    @OrderBy(value = "id asc")
    private Set<File> planFiles;

    //step 4

    //Ҳаво бассейнини атмосфера хавосини ифлослантирувчи манбалар ҳақида маьлумот
    @ManyToMany
    @JoinTable(name = "category_four_additional_jt_air_pool",
            joinColumns = { @JoinColumn(name = "category_four_additional") },
            inverseJoinColumns = { @JoinColumn(name = "air_pool")})
    @OrderBy(value = "id asc")
    private Set<AirPool> airPools;


    //step 4.3

    //Зарарли моддаларни умумий миқдори, уларни тозалаш ва фойдаланиш
    @ManyToMany
    @JoinTable(name = "category_four_additional_jt_harmful_substances_amount",
            joinColumns = { @JoinColumn(name = "category_four_additional") },
            inverseJoinColumns = { @JoinColumn(name = "harmful_substances_amount")})
    @OrderBy(value = "id asc")
    private Set<HarmfulSubstancesAmount> harmfulSubstancesAmounts;

    //Қаттиқ ташламалар

    @Column(name = "hard_value_one", precision = 20, scale = 2)
    private Double hardValueOne;

    @Column(name = "hard_value_two", precision = 20, scale = 2)
    private Double hardValueTwo;

    @Column(name = "hard_value_three", precision = 20, scale = 2)
    private Double hardValueThree;

    @Column(name = "hard_value_four", precision = 20, scale = 2)
    private Double hardValueFour;

    @Column(name = "hard_value_five", precision = 20, scale = 2)
    private Double hardValueFive;

    //Газ ва суюқ ҳолдаги ташламалар

    @Column(name = "liquid_value_one", precision = 20, scale = 2)
    private Double liquidValueOne;

    @Column(name = "liquid_value_two", precision = 20, scale = 2)
    private Double liquidValueTwo;

    @Column(name = "liquid_value_three", precision = 20, scale = 2)
    private Double liquidValueThree;

    @Column(name = "liquid_value_our", precision = 20, scale = 2)
    private Double liquidValueFour;

    @Column(name = "liquid_value_five", precision = 20, scale = 2)
    private Double liquidValueFive;

    //step5
    //Чиқиндилар ҳисоби
    //Объектнинг иш фаолияти натижасида хосил буладиган чикиндилар тури ва микдори
    @Column(name = "about_waste", columnDefinition = "TEXT")
    private String aboutWaste;

    //Сув таъминоти ва оқава сув тизими
    // Сув таьминоти манбаси номи (мавжуд туман водопровод тизими ёки артезиан кудук) ва хажми
    @Column(name = "water_volume", columnDefinition = "TEXT")
    private String waterVolume;

    //Окава сувларини ташлаш манбаси (канализация, гидроизоляцияланган хандак ва б.) ва хажми
    @Column(name = "source_water", columnDefinition = "TEXT")
    private String sourceWater;

    //Иситиш тизими
    @Column(name = "heating_system", columnDefinition = "TEXT")
    private String heatingSystem;

    //Ёнғинга қарши тадбирлар
    @Column(name = "firefighting_measures", columnDefinition = "TEXT")
    private String firefightingMeasures;

    //Вентиляция
    @Column(name = "ventilation", columnDefinition = "TEXT")
    private String ventilation;

    //Фавқулодда вазиятларга қарши тадбирлар
    @Column(name = "emergency_measures", columnDefinition = "TEXT")
    private String emergencyMeasures;

    //Qozon nomi
    @Column(name = "boiler_name")
    private String boilerName;

    //Қозон характеристикаси
    @ManyToMany
    @JoinTable(name = "category_four_additional_jt_boiler_characteristics",
            joinColumns = { @JoinColumn(name = "category_four_additional") },
            inverseJoinColumns = { @JoinColumn(name = "boiler_characteristics")})
    @OrderBy(value = "id asc")
    private Set<BoilerCharacteristics> boilerCharacteristics;

    //step6
    //Атроф муҳитни ифлосланишини камайтирувчи тадбирлар
    @ManyToMany
    @JoinTable(name = "category_four_additional_jt_events",
            joinColumns = { @JoinColumn(name = "category_four_additional") },
            inverseJoinColumns = { @JoinColumn(name = "event_id") })
    @OrderBy(value = "id asc")
    private Set<PollutionMeasures> pollutionMeasures;

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

    //Чиқариладиган ташланмалар миқдори ва таркиби
    @Column(name = "discard_amount_about", columnDefinition = "TEXT")
    private String discardAmountAbout;

    //Чиқиндилар миқдори ва уларни жойлаштириш шартлари
    @Column(name = "waste_amount_about", columnDefinition = "TEXT")
    private String wasteAmountAbout;

    //Табиатни муҳофаза қилиш тадбирлари
    @Column(name = "events", columnDefinition = "TEXT")
    private String events;

    //ilova fayllar
    @ManyToMany
    @JoinTable(name = "conclusion_jt_files",
            joinColumns = { @JoinColumn(name = "files") },
            inverseJoinColumns = { @JoinColumn(name = "file_id") })
    @OrderBy(value = "id desc")
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
