package uz.maroqand.ecology.cabinet.controller.mgmt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import uz.maroqand.ecology.cabinet.constant.mgmt.MgmtTemplates;
import uz.maroqand.ecology.cabinet.constant.mgmt.MgmtUrls;
import uz.maroqand.ecology.core.constant.sys.TableHistoryEntity;
import uz.maroqand.ecology.core.constant.sys.TableHistoryType;
import uz.maroqand.ecology.core.entity.sys.Translation;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.sys.TableHistoryService;
import uz.maroqand.ecology.core.service.sys.TranslationService;
import uz.maroqand.ecology.core.service.user.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
public class TranslationsController {

    private final TranslationService translationService;
    private final TableHistoryService tableHistoryService;
    private final UserService userService;
    private final ObjectMapper objectMapper;
    @Autowired
    public TranslationsController(TranslationService translationService, TableHistoryService tableHistoryService, UserService userService, ObjectMapper objectMapper) {
        this.translationService = translationService;
        this.tableHistoryService = tableHistoryService;
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    @RequestMapping(MgmtUrls.TranslationsList)
    public String getTranslationsList() {
        return MgmtTemplates.TranslationsList;
    }
    @RequestMapping(value = MgmtUrls.TranslationsListAjax, produces = "application/json")
    @ResponseBody
    public HashMap<String, Object> getTranslationsAjaxList(
            @RequestParam(name = "translationTag", required = false) String translationTag,
            @RequestParam(name = "translationRu", required = false) String translationRu,
            @RequestParam(name = "translationUz", required = false) String translationUz,
            @RequestParam(name = "translationEn", required = false) String translationEn,
            @RequestParam(name = "translationOz", required = false) String translationOz,
            Pageable pageable
    ) {
        translationTag = StringUtils.trimToNull(translationTag);
        translationRu = StringUtils.trimToNull(translationRu);
        translationUz = StringUtils.trimToNull(translationUz);
        translationEn = StringUtils.trimToNull(translationEn);
        translationOz = StringUtils.trimToNull(translationOz);

        Page<Translation> translationsPage = translationService.findFiltered(translationTag, translationRu, translationUz, translationEn, translationOz, pageable);

        HashMap<String, Object> result = new HashMap<>();
        result.put("recordsTotal", translationsPage.getTotalElements()); //Total elements
        result.put("recordsFiltered", translationsPage.getTotalElements()); //Filtered elements

        List<Translation> translations = translationsPage.getContent();

        List<Object[]> convenientForJSONArray = new ArrayList<>(translations.size());

        for(Translation translation: translations) {
            convenientForJSONArray.add(new Object[]{
                    translation.getId(),
                    translation.getName(),
                    translation.getUzbek(),
                    translation.getOzbek(),
                    translation.getRussian(),
                    translation.getEnglish()
            });
        }

        result.put("data", convenientForJSONArray);
        return result;
    }

    @RequestMapping(value = MgmtUrls.TranslationsSearchByTag,produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public HashMap<String,String> conceptsSearchTagAjax(@RequestParam(name = "name") String nameTag){
        Translation translation = translationService.findByName(nameTag);
        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        System.out.println(locale);
        HashMap<String,String> result = new HashMap<>();
        if (translation != null)
            result.put("data",translation.getNameTranslation(locale));
        return result;
    }

    @RequestMapping(MgmtUrls.TranslationsNew)
    public String newTranslation(Model model) {
        Translation translation = new Translation();
        model.addAttribute("translation", translation);
        model.addAttribute("action_url", MgmtUrls.TranslationsCreate);
        return MgmtTemplates.TranslationsNew;
    }

    @RequestMapping(MgmtUrls.TranslationsEdit)
    public String editTranslation(
            Model model,
            @RequestParam(name = "id", required = true) Integer translationId
    ) {
        Translation translation = translationService.getById(translationId);
        if (translation == null) {
            return "redirect:" + MgmtUrls.TranslationsList;
        }

        model.addAttribute("translation", translation);
        model.addAttribute("action_url", MgmtUrls.TranslationsUpdate);

        return MgmtTemplates.TranslationsNew;
    }


    @RequestMapping(MgmtUrls.TranslationsCreate)
    public String createTranslation(Translation translation) {
        User user = userService.getCurrentUserFromContext();
        String after = "";
        translation.setName(translation.getName().trim());
        Translation byName = translationService.findByName(translation.getName());
        if (byName != null) {
            return "redirect:" + MgmtUrls.TranslationsList;
        }

        translation.setUzbek(translation.getUzbek().trim());
        translation.setOzbek(translation.getOzbek().trim());
        translation.setRussian(translation.getRussian().trim());
        translation.setEnglish(translation.getEnglish().trim());

        translationService.create(translation);
        translationService.updateByName(translation.getName());
        try {
            after = objectMapper.writeValueAsString(translation);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        tableHistoryService.create(
                TableHistoryType.add,
                TableHistoryEntity.Translation,
                translation.getId(),
                null,
                after,
                "",
                user.getId(),
                user.getUserAdditionalId());

        return "redirect:" + MgmtUrls.TranslationsList;
    }

    @RequestMapping(MgmtUrls.TranslationsUpdate)
    public String updateTranslation(Translation translation) {
        User user = userService.getCurrentUserFromContext();
        String oldTranslationStr="";
        String newTranslationStr="";
        Translation oldTranslation = translationService.getById(translation.getId());
        if (oldTranslation == null) {
            return "redirect:" + MgmtUrls.TranslationsList;
        }
        try {
            oldTranslationStr = objectMapper.writeValueAsString(oldTranslation);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        oldTranslation.setName(translation.getName().trim());
        oldTranslation.setUzbek(translation.getUzbek().trim());
        oldTranslation.setOzbek(translation.getOzbek().trim());
        oldTranslation.setRussian(translation.getRussian().trim());
        oldTranslation.setEnglish(translation.getEnglish().trim());
        translationService.update(oldTranslation);
        translationService.updateByName(translation.getName());
        try {
            newTranslationStr = objectMapper.writeValueAsString(oldTranslation);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        tableHistoryService.create(
                TableHistoryType.edit,
                TableHistoryEntity.Translation,
                translation.getId(),
                oldTranslationStr,
                newTranslationStr,
                "",
                user.getId(),
                user.getUserAdditionalId());
        return "redirect:" + MgmtUrls.TranslationsList;
    }
}
