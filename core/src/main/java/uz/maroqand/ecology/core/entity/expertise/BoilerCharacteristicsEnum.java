package uz.maroqand.ecology.core.entity.expertise;

import java.util.*;

public enum BoilerCharacteristicsEnum {                                                                     //nomi                          type
    DurationOfHeatingDays(0,"sys_boiler.duration_of_heating_days","sys_boiler.type_0"),//Иситиш кунлари давомийлиги      кун
    HeatCapacity(1,"sys_boiler.heat_capacity","sys_boiler.type_1"),  //Иссиқлик қуввати                                  кВт    (ккал/соат)
    HeatingSurface(2,"sys_boiler.heating_surface","sys_boiler.type_2"),  //Иситиш юзаси     metr kvadrat
    WaterPressureNormal(3,"sys_boiler.water_pressure_normal","sys_boiler.type_3"), //Сувнинг номинал босими                 МПа  (кгс/см )
    WaterPressureMax(4,"sys_boiler.water_pressure_max","sys_boiler.type_4"), //Сувнинг максимал босими                     МПа  (кгс/см )
    WaterLength(5,"sys_boiler.water_length","sys_boiler.type_5"), //Сув ҳажми                                       litr
    NominalGasConsumption(6,"sys_boiler.nominal_gas_consumption","sys_boiler.type_6"),//Номинал газ сарфи
    BoilerMass(7,"sys_boiler.boiler_mass","sys_boiler.type_7"),//sys_boiler.boiler_mass             кг
    ФИК(8,"sys_boiler.ФИК","sys_boiler.type_8");//ФИК   %

    private Integer id;
    private String name;
    private String type;

    BoilerCharacteristicsEnum(Integer id, String name, String type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }


    private static Map<Integer, BoilerCharacteristicsEnum> boilerCharacteristicMap;
    static {
        boilerCharacteristicMap = new HashMap<>();
        for (BoilerCharacteristicsEnum characteristics : BoilerCharacteristicsEnum.values()) {
            boilerCharacteristicMap.put(characteristics.getId(), characteristics);
        }
    }

    public static BoilerCharacteristicsEnum getBoilerCharacteristicById(Integer id) {
        return boilerCharacteristicMap.get(id);
    }

    public static List<BoilerCharacteristicsEnum> getBoilerCharacteristics() {
        List<BoilerCharacteristicsEnum> boilerCharacteristicList = new LinkedList<>(Arrays.asList(BoilerCharacteristicsEnum.values()));
        return boilerCharacteristicList;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
