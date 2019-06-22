package uz.maroqand.ecology.cabinet.controller.expertise_mgmt;

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
import uz.maroqand.ecology.core.entity.expertise.Material;
import uz.maroqand.ecology.core.service.expertise.MaterialService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
public class MaterialController {
    private final MaterialService materialService;

    @Autowired
    public MaterialController(MaterialService materialService){
        this.materialService = materialService;
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

    @RequestMapping(value = ExpertiseMgmtUrls.MaterialNew, method = RequestMethod.POST)
    public String materialSubmit(Material material){
        materialService.save(material);
        return "redirect:" + ExpertiseMgmtUrls.MaterialList;
    }

    @RequestMapping(ExpertiseMgmtUrls.MaterialNew)
    public String materialNew(Model model){
        Material material = new Material();
        model.addAttribute("material", material);
        return ExpertiseMgmtTemplates.MaterialNew;
    }
}
