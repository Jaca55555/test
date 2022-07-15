package uz.maroqand.ecology.core.entity.expertise;

import java.util.*;

public enum BoilerCharacteristicsEnum {                                                                     //nomi                          type
    TONNA(0,"tonna","sys_boiler.type_0"),//Иситиш кунлари давомийлиги      кун
    M3(1,"m3","sys_boiler.type_1"),  //Иссиқлик қуввати                                  кВт    (ккал/соат)
    KG(2,"kg","sys_boiler.type_2"); //Иситиш юзаси     metr kvadrat

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
        return new LinkedList<>(Arrays.asList(BoilerCharacteristicsEnum.values()));
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
