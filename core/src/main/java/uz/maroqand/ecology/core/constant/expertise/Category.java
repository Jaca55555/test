package uz.maroqand.ecology.core.constant.expertise;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Utkirbek Boltaev on 14.06.2019.
 * (uz)
 * (ru)
 */
public enum Category {

    Category1(1,"sys_category1"),
    Category2(2,"sys_category2"),
    Category3(3,"sys_category3"),
    Category4(4,"sys_category4");

    private Integer id;
    private String name;

    Category(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    private static Map<Integer, Category> categoryMap;
    static {
        categoryMap = new HashMap<>();
        for (Category category : Category.values()) {
            categoryMap.put(category.getId(), category);
        }
    }

    public static Category getCategory(Integer id) {
        return categoryMap.get(id);
    }

    public static List<Category> getCategoryList() {
        List<Category> categoryList = new LinkedList<>();
        for (Category category : Category.values()) {
            categoryList.add(category);
        }
        return categoryList;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
