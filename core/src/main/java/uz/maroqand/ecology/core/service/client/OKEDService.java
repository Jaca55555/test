package uz.maroqand.ecology.core.service.client;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import uz.maroqand.ecology.core.entity.client.OKED;
import uz.maroqand.ecology.core.repository.client.OKEDRepository;

import java.util.*;

@Service
public class OKEDService {

    private OKEDRepository okedRepository;

    private HashMap<Integer, List<OKED>> okedTree;

    private Gson gson = new Gson();

    //Bu okedlar aslida ta'qiqlangan, faqat davlat tashkilotlari uchun,
    //shuning uchun ular keyin qo'shildi
    private List<OKED> specialOkeds;

    //Ishlatilishi mumkin bo`lgan -- to'g'ri OKED idlari ro'yxati
    //Bu ro'yxatda yo'q bo'lgan har qanday OKED ID kodi xato deb hisoblanadi va JSON massivlardan tozalanadi.
    private Set<Integer> regularOkedIds;

    @Autowired
    public OKEDService(OKEDRepository okedRepositoryForInitialization) {
        okedRepository = okedRepositoryForInitialization;

        regularOkedIds = new HashSet<>();
        List<OKED> okeds = okedRepository.findByOrderByIdAsc();
        okedTree = new HashMap<>();
        Integer parentId, parentId2;
        for (OKED oked : okeds) {
            parentId = null;
            parentId2 = null;

            if (oked.getLevel() == 11) {
                parentId = oked.getParentTypeId();

                if (oked.getTypeName() != null && !oked.getTypeName().isEmpty()) {
                    parentId2 = oked.getParentGroupId();
                }
                regularOkedIds.add(oked.getId());

            } else if (oked.getLevel() == 10) {
                parentId = oked.getParentGroupId();

            } else if (oked.getLevel() == 3) {
                parentId = oked.getParentDivisionId();

            } else if (oked.getLevel() == 2) {
                parentId = oked.getParentSectionId();
            } else if (oked.getLevel() == 1) {
                parentId = 0;
            }
            if (parentId != null) {
                if (!okedTree.containsKey(parentId)) {
                    okedTree.put(parentId, new LinkedList<OKED>());
                }
                okedTree.get(parentId).add(oked);
            }

            if (parentId2 != null) {
                if (!okedTree.containsKey(parentId2)) {
                    okedTree.put(parentId2, new LinkedList<OKED>());
                }
                okedTree.get(parentId2).add(oked);
            }
        }

        //Maxsus- keyin qo'shilgan okedlarni IDlari >= 2000
        specialOkeds = okedRepository.findByIdIsGreaterThan(1999);
    }

    public HashMap<Integer, List<OKED>> getOKEDTree() {
        return okedTree;
    }

    @Cacheable(value = "OKEDServoce.getById",key = "#okedId", condition="#okedId != null", unless="#result == null")
    public OKED getById(Integer okedId) {
        if(okedId==null) return null;
        return okedRepository.getOne(okedId);
    }

    @Cacheable("OKEDServoce.getOkedIdToNumberMap")
    public Map<Integer, String> getOkedIdToNumberMap() {
        List<OKED> okeds = okedRepository.findAll();

        Map<Integer, String> result = new HashMap<>();

        for (OKED oked : okeds) {
            if (oked.getSubtypeName() != null && !oked.getSubtypeName().isEmpty()) {
                result.put(oked.getId(), oked.getSubtypeName());
            }
        }

        return result;
    }

    @Cacheable("OKEDService.getOKEDTreeForFrontend")
    public Map<String, Object> getOKEDTreeForFrontend(String locale) {
        List<String> locales = Arrays.asList("ru", "uz", "en");

        if (!locales.contains(locale)) locale = "ru";

        Map<Integer, List<OKED>> okedTree = getOKEDTree();

        Map<String, Object> newOkedTree = new HashMap<>();
        Map<Integer, String> namesMap = new HashMap<>();
        Map<Integer, List<Integer>> graphMap = new HashMap<>();
        Map<Integer, Integer> parentMap = new HashMap<>();
        Set<Integer> idsSet = new HashSet<>();
        List<Integer> idsList = new LinkedList<>();
        List<Integer> licenceRequiredOkedIds = new LinkedList<>();


        for(Integer key: okedTree.keySet()) {
            List<OKED> okeds = okedTree.get(key);
            List<Integer> okedForFrontendList = new ArrayList<>(okeds.size());

            for(OKED oked : okeds) {
                if(idsSet.add(oked.getId())) {
                    idsList.add(oked.getId());
                }
                namesMap.put(oked.getId(), oked.getNameTranslation(locale));
                parentMap.put(oked.getId(), key);
                okedForFrontendList.add(oked.getId());
                if (oked.getRequiresLicence()) licenceRequiredOkedIds.add(oked.getId());
            }

            graphMap.put(key, okedForFrontendList);
        }

        for (OKED oked : specialOkeds) {
            namesMap.put(oked.getId(), oked.getNameTranslation(locale));
        }
        newOkedTree.put("ids", idsList);
        newOkedTree.put("names", namesMap);
        newOkedTree.put("graph", graphMap);
        newOkedTree.put("parents", parentMap);
        newOkedTree.put("licenced_okeds", licenceRequiredOkedIds);



        return newOkedTree;
    }

    @Cacheable("OKEDService.getSpecialOkeds")
    public Map<String, Object> getSpecialOkeds(String locale) {
        List<String> locales = Arrays.asList("ru", "uz", "en");

        if (!locales.contains(locale)) locale = "ru";

        Map<Integer, String> namesMap = new HashMap<>();
        List<Integer> idsList = new LinkedList<>();
        for (OKED oked : specialOkeds) {
            idsList.add(oked.getId());
        }
        Map<String,Object> result = new HashMap<>();
        result.put("names", namesMap);
        result.put("ids", idsList);

        return result;
    }

    private OKED searchOked(String code) {
        List<OKED> okedList = okedRepository.findBySubtypeName(code);
        if(okedList.size()>0){
            return okedList.get(0);
        }else{
            return null;
        }
    }

    /**
     * JSON formatida kelgan OKED idlari massivini noto'g'ri qiymatlardan tozalaydi.
     * To'g'ri qiymatlarni bazadan olib tekshiradi.
     * @param okedListInJSON berilgan OKED idlari massivi JSON formatida
     * @return tozalangan OKED idlari massivi JSON formatida
     */
    public String clearOkedListFromWrongValues(String okedListInJSON) {
        try {
            List<Integer> additionalOkedsList = gson.fromJson(okedListInJSON, new TypeToken<List<Integer>>() {
            }.getType());
            if (CollectionUtils.isEmpty(additionalOkedsList)) {
                return "[]";
            }
            List<Integer> correctIdsList = new LinkedList<>();
            for (Integer okedId : additionalOkedsList) {
                // Oddiy OKED kodlari ichida bu ID yo'q bo'lsa, demak u noto'g'ri / xato, uni hisobga olmaymiz.
                if (!regularOkedIds.contains(okedId)) continue;
                correctIdsList.add(okedId);
            }
            return gson.toJson(correctIdsList);
        } catch (JsonSyntaxException ex) {
            return "[]";
        }
    }

    public Map<String, String> returnSearchOkedResultAsMap(String code) {
        HashMap<String, String> result = new HashMap<>();
        OKED oked = searchOked(code);
        if(oked==null){
            oked = new OKED();
        }
        result.put("id", String.valueOf(oked.getId()));
        result.put("name", oked.getName());
        return result;
    }

    /**
     * OKED ni oked kodi yordamida qidiradi.
     * @param okedCode 011213 ko`rinishida yozilgan oked kod.
     * @return berilgan kod bo`yicha oked kod topilsa, OKED obyektini qaytaradi. Aks holda null.
     */
    public OKED getOkedFromOkedV1Code(String okedCode) {
        if (okedCode == null || okedCode.length() != 5) {
            return null;
        }
        try {
            String okedString = okedCode.substring(0, 2) + "." + okedCode.substring(2, 4) + "." + okedCode.substring(4, 5);
            return searchOked(okedString);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Berilgan OKED v1 idlardan okedlar nomlari ro'yxatini tuzib beradi.
     *
     * @param okedListInJSON berilgan OKED idlari massivi JSON formatida
     * @return oked nomlari ro'yxati
     */
    public String getOkedNamesFromOkedIdsList(String okedListInJSON) {
        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        try {
            List<Integer> additionalActivitiesIds = gson.fromJson(okedListInJSON, new TypeToken<List<Integer>>() {
            }.getType());
            List<OKED> individualsActivityTypes = okedRepository.findByIdIn(additionalActivitiesIds);
            if (CollectionUtils.isEmpty(individualsActivityTypes)) {
                return null;
            }
            StringBuilder activitiesStr = new StringBuilder();
            for (OKED activityType : individualsActivityTypes) {
                activitiesStr
                        .append(activityType.getNameTranslation(locale))
                        .append(", ");
            }

            if (activitiesStr.length() > 0) {
                activitiesStr.setLength(activitiesStr.length() - 2);
            }

            return activitiesStr.toString();
        } catch (Exception ignored) {
            return null;
        }
    }

}
