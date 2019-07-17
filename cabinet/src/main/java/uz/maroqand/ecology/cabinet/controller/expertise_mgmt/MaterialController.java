package uz.maroqand.ecology.cabinet.controller.expertise_mgmt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import uz.maroqand.ecology.core.service.user.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
public class MaterialController {
    private final MaterialService materialService;
    private final TableHistoryService tableHistoryService;
    private final ObjectMapper objectMapper;
    private final UserService userService;

    @Autowired
    public MaterialController(MaterialService materialService, TableHistoryService tableHistoryService, ObjectMapper objectMapper, UserService userService){
        this.materialService = materialService;
        this.tableHistoryService = tableHistoryService;
        this.objectMapper = objectMapper;
        this.userService = userService;
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
                    material.getName(),
                    material.getNameRu()
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
}
