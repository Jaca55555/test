package uz.maroqand.ecology.cabinet.controller.expertise_mgmt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uz.maroqand.ecology.cabinet.constant.expertise_mgmt.ExpertiseMgmtTemplates;
import uz.maroqand.ecology.cabinet.constant.expertise_mgmt.ExpertiseMgmtUrls;
import uz.maroqand.ecology.core.constant.sys.TableHistoryEntity;
import uz.maroqand.ecology.core.constant.sys.TableHistoryType;
import uz.maroqand.ecology.core.entity.expertise.ObjectExpertise;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.expertise.ObjectExpertiseService;
import uz.maroqand.ecology.core.service.sys.TableHistoryService;
import uz.maroqand.ecology.core.service.user.UserService;

import java.lang.reflect.Type;
import java.util.*;

@Controller
public class ObjectExpertiseController {

    private final ObjectExpertiseService objectExpertiseService;
    private final TableHistoryService tableHistoryService;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    @Autowired
    public ObjectExpertiseController(
            ObjectExpertiseService objectExpertiseService,
            TableHistoryService tableHistoryService,
            UserService userService,
            ObjectMapper objectMapper
    ) {
        this.objectExpertiseService = objectExpertiseService;
        this.tableHistoryService = tableHistoryService;
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    @RequestMapping(value = ExpertiseMgmtUrls.ObjectExpertiseList,method = RequestMethod.GET)
    public String getList(){
        return ExpertiseMgmtTemplates.ObjectExpertiseList;
    }

    @RequestMapping(value = ExpertiseMgmtUrls.ObjectExpertiseList,method = RequestMethod.POST)
    @ResponseBody
    public HashMap<String, Object>  getAjaxList(
            @RequestParam(name = "id",required = false) Integer id,
            @RequestParam(name = "name",required = false) String name,
            @RequestParam(name = "nameOz",required = false) String nameOz,
            @RequestParam(name = "nameEn",required = false) String nameEn,
            @RequestParam(name = "nameRu",required = false) String nameRu,
            Pageable pageable
    ){
        name = StringUtils.trimToNull(name);
        nameOz = StringUtils.trimToNull(nameOz);
        nameEn = StringUtils.trimToNull(nameEn);
        nameRu = StringUtils.trimToNull(nameRu);
        HashMap<String, Object> result = new HashMap<>();
        Page<ObjectExpertise> objectExpertisePage = objectExpertiseService.findFiltered(id,name,nameOz,nameEn,nameRu,pageable, false);

        result.put("recordsTotal", objectExpertisePage.getTotalElements()); //Total elements
        result.put("recordsFiltered", objectExpertisePage.getTotalElements()); //Filtered elements

        List<ObjectExpertise> objectExpertiseList = objectExpertisePage.getContent();
        List<Object[]> convenientForJSONArray = new ArrayList<>(objectExpertiseList.size());

        for(ObjectExpertise objectExpertise: objectExpertiseList) {
            convenientForJSONArray.add(new Object[]{
                    objectExpertise.getId(),
                    objectExpertise.getName(),
                    objectExpertise.getNameOz(),
                    objectExpertise.getNameEn(),
                    objectExpertise.getNameRu(),
            });
        }
        result.put("data", convenientForJSONArray);
        return result;
    }

    @RequestMapping(value = ExpertiseMgmtUrls.ObjectExpertiseNew)
    public String objectExpertiseNew(Model model){
        ObjectExpertise objectExpertise = new ObjectExpertise();

        model.addAttribute("objectExpertise",objectExpertise);
        model.addAttribute("action_url",ExpertiseMgmtUrls.ObjectExpertiseCreate);
        model.addAttribute("back_url",ExpertiseMgmtUrls.ObjectExpertiseList);
        return ExpertiseMgmtTemplates.ObjectExpertiseNew;
    }

    @RequestMapping(value = ExpertiseMgmtUrls.ObjectExpertiseCreate)
    public String objectExpertiseCreate(ObjectExpertise objectExpertise){
        User user = userService.getCurrentUserFromContext();
        ObjectExpertise objectExpertise1 = new ObjectExpertise();
        objectExpertise1.setName(objectExpertise.getName());
        objectExpertise1.setNameOz(objectExpertise.getNameOz());
        objectExpertise1.setNameEn(objectExpertise.getNameEn());
        objectExpertise1.setNameRu(objectExpertise.getNameRu());
        objectExpertise1.setDeleted(false);
        objectExpertise1 = objectExpertiseService.save(objectExpertise1);
        String after = "";
        try {
            after = objectMapper.writeValueAsString(objectExpertise1);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        tableHistoryService.create(
                TableHistoryType.add,
                TableHistoryEntity.ObjectExpertise,
                objectExpertise1.getId(),
                null,
                after,
                "",
                user.getId(),
                user.getUserAdditionalId()
        );

        return "redirect:" + ExpertiseMgmtUrls.ObjectExpertiseList;
    }

    @RequestMapping(value = ExpertiseMgmtUrls.ObjectExpertiseEdit)
    public String objectExpertiseEdit(
            @RequestParam(name = "id") Integer id,
            Model model
    ){
        ObjectExpertise objectExpertise = objectExpertiseService.getById(id);
        if (objectExpertise==null){
            return "redirect:" + ExpertiseMgmtUrls.ObjectExpertiseList;
        }
        model.addAttribute("objectExpertise",objectExpertise);
        model.addAttribute("action_url",ExpertiseMgmtUrls.ObjectExpertiseUpdate);
        model.addAttribute("back_url",ExpertiseMgmtUrls.ObjectExpertiseList);

        return ExpertiseMgmtTemplates.ObjectExpertiseNew;
    }

    @RequestMapping(value = ExpertiseMgmtUrls.ObjectExpertiseUpdate)
    public String objectExpertiseUpdate(
            @RequestParam(name = "id") Integer id,
            ObjectExpertise objectExpertise
    ){
        User user = userService.getCurrentUserFromContext();
        ObjectExpertise objectExpertise1 = objectExpertiseService.getById(id);
        if (objectExpertise1==null){
            return "redirect:" + ExpertiseMgmtUrls.ObjectExpertiseList;
        }
        String oldObjectExpertise = "";
        try {
            oldObjectExpertise = objectMapper.writeValueAsString(objectExpertise1);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        objectExpertise1.setName(objectExpertise.getName());
        objectExpertise1.setNameOz(objectExpertise.getNameOz());
        objectExpertise1.setNameEn(objectExpertise.getNameEn());
        objectExpertise1.setNameRu(objectExpertise.getNameRu());
        objectExpertise1.setUpdateBy(user.getId());
        objectExpertise1.setUpdateAt(new Date());

        objectExpertise1 = objectExpertiseService.save(objectExpertise1);
        String after = "";
        try {
            after = objectMapper.writeValueAsString(objectExpertise1);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        tableHistoryService.create(
                TableHistoryType.edit,
                TableHistoryEntity.ObjectExpertise,
                objectExpertise1.getId(),
                oldObjectExpertise,
                after,
                "",
                user.getId(),
                user.getUserAdditionalId()
        );
        return "redirect:" + ExpertiseMgmtUrls.ObjectExpertiseList;
    }

    @RequestMapping(ExpertiseMgmtUrls.ObjectExpertiseView)
    public String getObjectExpertiseViewPage(
            @RequestParam(name = "id") Integer id,
            Model model
    ){
        ObjectExpertise objectExpertise = objectExpertiseService.getById(id);
        if (objectExpertise==null){
            return "redirect:" + ExpertiseMgmtUrls.ObjectExpertiseList;
        }
        Type type = new TypeToken<List<ObjectExpertise>>(){}.getType();
        List<HashMap<String,Object>> beforeAndAfterList = tableHistoryService.forAudit(type,TableHistoryEntity.ObjectExpertise,id);

        model.addAttribute("objectExpertise",objectExpertise);
        model.addAttribute("beforeAndAfterList",beforeAndAfterList);
        return ExpertiseMgmtTemplates.ObjectExpertiseView;
    }

    @RequestMapping(value = ExpertiseMgmtUrls.ObjectExpertiseDelete, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public HashMap<String, Object> deleteObjectExpertise(@RequestParam("id") String id, @RequestParam("msg") String msg)
    {
        User user = userService.getCurrentUserFromContext();
        ObjectExpertise objectExpertise = objectExpertiseService.getById(Integer.valueOf(id));
        HashMap<String, Object> response = new HashMap<>();
        if (objectExpertise!=null){
            objectExpertiseService.delete(objectExpertise, user.getId(), msg);
            response.put("status", "success");
            response.put("id", id);
        } else {
            response.put("status", "error");
        }
        return response;
    }
}
