package uz.maroqand.ecology.cabinet.controller.mgmt;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
import uz.maroqand.ecology.core.entity.expertise.ObjectExpertise;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.expertise.ObjectExpertiseService;
import uz.maroqand.ecology.core.service.sys.TableHistoryService;
import uz.maroqand.ecology.core.service.user.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
public class ObjectExpertiseController {

    private final ObjectExpertiseService objectExpertiseService;
    private final TableHistoryService tableHistoryService;
    private final UserService userService;
    private final Gson gson;

    @Autowired
    public ObjectExpertiseController(ObjectExpertiseService objectExpertiseService, TableHistoryService tableHistoryService, UserService userService, Gson gson) {
        this.objectExpertiseService = objectExpertiseService;
        this.tableHistoryService = tableHistoryService;
        this.userService = userService;
        this.gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    }

    @RequestMapping(value = MgmtUrls.ObjectExpertiseList,method = RequestMethod.GET)
    public String getList(){return MgmtTemplates.ObjectExpertiseList;}

    @RequestMapping(value = MgmtUrls.ObjectExpertiseList,method = RequestMethod.POST)
    @ResponseBody
    public HashMap<String, Object>  getAjaxList(
            @RequestParam(name = "id",required = false) Integer id,
            @RequestParam(name = "name",required = false) String name,
            @RequestParam(name = "nameRu",required = false) String nameRu,
            Pageable pageable
    ){
        System.out.println("keldi");
        name = StringUtils.trimToNull(name);
        nameRu = StringUtils.trimToNull(nameRu);
        HashMap<String, Object> result = new HashMap<>();
        Page<ObjectExpertise> objectExpertisePage = objectExpertiseService.findFiltered(id,name,nameRu,pageable);

        result.put("recordsTotal", objectExpertisePage.getTotalElements()); //Total elements
        result.put("recordsFiltered", objectExpertisePage.getTotalElements()); //Filtered elements

        List<ObjectExpertise> objectExpertiseList = objectExpertisePage.getContent();

        List<Object[]> convenientForJSONArray = new ArrayList<>(objectExpertiseList.size());

        for(ObjectExpertise objectExpertise: objectExpertiseList) {
            convenientForJSONArray.add(new Object[]{
                    objectExpertise.getId(),
                    objectExpertise.getName(),
                    objectExpertise.getNameRu(),
            });
        }
        result.put("data", convenientForJSONArray);

        return result;
    }

    @RequestMapping(value = MgmtUrls.ObjectExpertiseNew)
    public String objectExpertiseNew(Model model){
        ObjectExpertise objectExpertise = new ObjectExpertise();

        model.addAttribute("objectExpertise",objectExpertise);
        model.addAttribute("action_url",MgmtUrls.ObjectExpertiseCreate);
        model.addAttribute("back_url",MgmtUrls.ObjectExpertiseList);

        return MgmtTemplates.ObjectExpertiseNew;
    }

    @RequestMapping(value = MgmtUrls.ObjectExpertiseCreate)
    public String objectExpertiseCreate(ObjectExpertise objectExpertise){
        User user = userService.getCurrentUserFromContext();
        ObjectExpertise objectExpertise1 = new ObjectExpertise();
        objectExpertise1.setName(objectExpertise.getName());
        objectExpertise1.setNameRu(objectExpertise.getNameRu());
        objectExpertiseService.save(objectExpertise1);

        tableHistoryService.create(
                TableHistoryType.add,
                TableHistoryEntity.ObjectExpertise,
                objectExpertise1.getId(),
                null,
                gson.toJson(objectExpertise1),
                "",
                user.getId(),
                user.getUserAdditionalId()
        );

        return "redirect:" + MgmtUrls.ObjectExpertiseList;
    }

    @RequestMapping(value = MgmtUrls.ObjectExpertiseEdit)
    public String objectExpertiseEdit(
            @RequestParam(name = "id") Integer id,
            Model model
    ){
        ObjectExpertise objectExpertise = objectExpertiseService.getById(id);
        if (objectExpertise==null){
            return "redirect:" + MgmtUrls.ObjectExpertiseList;
        }
        model.addAttribute("objectExpertise",objectExpertise);
        model.addAttribute("action_url",MgmtUrls.ObjectExpertiseUpdate);
        model.addAttribute("back_url",MgmtUrls.ObjectExpertiseList);

        return MgmtTemplates.ObjectExpertiseNew;
    }

    @RequestMapping(value = MgmtUrls.ObjectExpertiseUpdate)
    public String objectExpertiseUpdate(
            @RequestParam(name = "id") Integer id,
            ObjectExpertise objectExpertise
    ){
        User user = userService.getCurrentUserFromContext();
        ObjectExpertise objectExpertise1 = objectExpertiseService.getById(id);
        if (objectExpertise1==null){
            return "redirect:" + MgmtUrls.ObjectExpertiseList;
        }
        String oldObjectExpertise = gson.toJson(objectExpertise1);
        objectExpertise1.setName(objectExpertise.getName());
        objectExpertise1.setNameRu(objectExpertise.getNameRu());
        objectExpertiseService.save(objectExpertise1);

        tableHistoryService.create(
                TableHistoryType.edit,
                TableHistoryEntity.ObjectExpertise,
                objectExpertise1.getId(),
                oldObjectExpertise,
                gson.toJson(objectExpertise1),
                "",
                user.getId(),
                user.getUserAdditionalId()
        );
        return "redirect:" + MgmtUrls.ObjectExpertiseList;
    }

}
