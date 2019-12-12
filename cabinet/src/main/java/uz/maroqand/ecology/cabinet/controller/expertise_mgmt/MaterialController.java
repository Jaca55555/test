package uz.maroqand.ecology.cabinet.controller.expertise_mgmt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import uz.maroqand.ecology.cabinet.constant.expertise_mgmt.ExpertiseMgmtTemplates;
import uz.maroqand.ecology.cabinet.constant.expertise_mgmt.ExpertiseMgmtUrls;
import uz.maroqand.ecology.core.constant.sys.TableHistoryEntity;
import uz.maroqand.ecology.core.constant.sys.TableHistoryType;
import uz.maroqand.ecology.core.entity.expertise.Material;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.expertise.MaterialService;
import uz.maroqand.ecology.core.service.sys.TableHistoryService;
import uz.maroqand.ecology.core.service.user.UserAdditionalService;
import uz.maroqand.ecology.core.service.user.UserService;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
public class MaterialController {
    private final MaterialService materialService;
    private final TableHistoryService tableHistoryService;
    private final ObjectMapper objectMapper;
    private final UserService userService;
    private final UserAdditionalService userAdditionalService;

    @Autowired
    public MaterialController(MaterialService materialService, TableHistoryService tableHistoryService, ObjectMapper objectMapper, UserService userService, UserAdditionalService userAdditionalService){
        this.materialService = materialService;
        this.tableHistoryService = tableHistoryService;
        this.objectMapper = objectMapper;
        this.userService = userService;
        this.userAdditionalService = userAdditionalService;
    }

    @RequestMapping(ExpertiseMgmtUrls.MaterialList)
    public String materialList(){
        return ExpertiseMgmtTemplates.MaterialList;
    }

    @RequestMapping(value = ExpertiseMgmtUrls.MaterialListAjax, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public HashMap<String,Object> materialListAjax(Pageable pageable){

        HashMap<String, Object> result = new HashMap<>();
        Page<Material> materialPage = materialService.getAll(pageable);
        result.put("recordsTotal", materialPage.getTotalElements()); //Total elements
        result.put("recordsFiltered", materialPage.getTotalElements()); //Filtered elements

        List<Material> materialList = materialPage.getContent();
        List<Object[]> convenientForJSONArray = new ArrayList<>(materialList.size());
        for (Material material : materialList){
            convenientForJSONArray.add(new Object[]{
                    material.getId(),
                    material.getName()+"  ("+(material.getNameShort()!=null?material.getNameShort():"")+")",
                    material.getNameOz()+"  ("+(material.getNameShortOz()!=null?material.getNameShortOz():"")+")",
                    material.getNameEn()+"  ("+(material.getNameShortEn()!=null?material.getNameShortEn():"")+")",
                    material.getNameRu()+"  ("+(material.getNameShortRu()!=null?material.getNameShortRu():"")+")"
            });
        }
        result.put("data",convenientForJSONArray);
        return result;
    }

    @RequestMapping(ExpertiseMgmtUrls.MaterialEdit)
    public String materialEdit(
            @RequestParam(name = "id") Integer id,
            Model model
    ){
        Material material = materialService.getById(id);
        if(material == null) return "redirect:" + ExpertiseMgmtUrls.MaterialList;
        model.addAttribute("material", material);
        return ExpertiseMgmtTemplates.MaterialNew;
    }

    //Create or Update
    @RequestMapping(value = ExpertiseMgmtUrls.MaterialNew, method = RequestMethod.POST)
    public String materialSubmit(Material material){

        User user = userService.getCurrentUserFromContext();
        String before = null;
        String after="";

        //Update
        if (material.getId()!=null){

            try {
                after = objectMapper.writeValueAsString(material);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            material = materialService.save(material);

            tableHistoryService.create(
                    TableHistoryType.add,
                    TableHistoryEntity.Material,
                    material.getId(),
                    before,
                    after,
                    "",
                    user.getId(),
                    user.getUserAdditionalId()
            );

        }else{//Create

            try {
                before = objectMapper.writeValueAsString(material);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            material = materialService.save(material);

            try {
                after = objectMapper.writeValueAsString(material);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            tableHistoryService.create(
                    TableHistoryType.edit,
                    TableHistoryEntity.Material,
                    material.getId(),
                    before,
                    after,
                    "",
                    user.getId(),
                    user.getUserAdditionalId()
            );

        }

        return "redirect:" + ExpertiseMgmtUrls.MaterialList;
    }

    @RequestMapping(ExpertiseMgmtUrls.MaterialNew)
    public String materialNew(Model model){
        Material material = new Material();
        model.addAttribute("material", material);
        return ExpertiseMgmtTemplates.MaterialNew;
    }

    @RequestMapping(ExpertiseMgmtUrls.MaterialView)
    public String getMaterialViewPage(
            @RequestParam(name = "id") Integer id,
            Model model
    ){
        Material material = materialService.getById(id);
        if (material==null){
            return "redirect:" + ExpertiseMgmtUrls.MaterialList;
        }
        Type type = new TypeToken<List<Material>>(){}.getType();
        List<HashMap<String,Object>> beforeAndAfterList = tableHistoryService.forAudit(type,TableHistoryEntity.Material,id);

        model.addAttribute("material",material);
        model.addAttribute("beforeAndAfterList",beforeAndAfterList);
        return ExpertiseMgmtTemplates.MaterialView;
    }

    @RequestMapping(
            value = ExpertiseMgmtUrls.MaterialDelete,
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public HashMap<String, String> deleteMaterial(
        @RequestParam(name = "id") Integer id,
        @RequestParam(name = "msg") String msg
    ){
        User user = userService.getCurrentUserFromContext();
        Material material = materialService.getById(id);
        HashMap<String, String> response = new HashMap<>();
        if (material!=null && !material.getDeleted()){
            materialService.delete(material, user.getId(), msg);
            response.put("status", "success");
            response.put("id", String.valueOf(id));
        } else {
            response.put("status", "error");
        }
        return response;
    }
}
