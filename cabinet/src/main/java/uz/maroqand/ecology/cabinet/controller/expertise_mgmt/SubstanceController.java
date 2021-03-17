package uz.maroqand.ecology.cabinet.controller.expertise_mgmt;

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
import uz.maroqand.ecology.core.constant.expertise.SubstanceType;
import uz.maroqand.ecology.core.entity.expertise.Substance;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.expertise.SubstanceService;
import uz.maroqand.ecology.core.service.user.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
public class SubstanceController {
    private final UserService userService;
    private final SubstanceService substanceService;

    public SubstanceController(UserService userService, SubstanceService substanceService) {
        this.userService = userService;
        this.substanceService = substanceService;
    }


    @RequestMapping(ExpertiseMgmtUrls.SubstanceList)
    public String substanceList(){
        return ExpertiseMgmtTemplates.SubstanceList;
    }

    @RequestMapping(value = ExpertiseMgmtUrls.SubstanceListAjax, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public HashMap<String,Object> substanceListAjax(Pageable pageable){

        HashMap<String, Object> result = new HashMap<>();
        Page<Substance> substancePage = substanceService.getAll(pageable);
        result.put("recordsTotal", substancePage.getTotalElements()); //Total elements
        result.put("recordsFiltered", substancePage.getTotalElements()); //Filtered elements

        List<Substance> substanceList = substancePage.getContent();
        List<Object[]> convenientForJSONArray = new ArrayList<>(substanceList.size());
        for (Substance substance : substanceList){
            convenientForJSONArray.add(new Object[]{
                    substance.getId(),
                    substance.getName(),
                    substance.getNameOz(),
                    substance.getNameEn(),
                    substance.getNameRu(),
                    substance.getType()!=null?substance.getType().getName():""
            });
        }
        result.put("data",convenientForJSONArray);
        return result;
    }

    @RequestMapping(ExpertiseMgmtUrls.SubstanceEdit)
    public String substanceEdit(
            @RequestParam(name = "id") Integer id,
            Model model
    ){
        Substance substance = substanceService.getById(id);
        if(substance == null) return "redirect:" + ExpertiseMgmtUrls.SubstanceList;
        model.addAttribute("substance", substance);
        model.addAttribute("substanceType", SubstanceType.getSubstanceTypeList());
        return ExpertiseMgmtTemplates.SubstanceNew;
    }

    //Create or Update
    @RequestMapping(value = ExpertiseMgmtUrls.SubstanceNew, method = RequestMethod.POST)
    public String substanceSubmit(Substance substance){

        User user = userService.getCurrentUserFromContext();
        String before = null;
        String after="";

        //Update
        if (substance.getId()!=null){
            substance = substanceService.save(substance);
            substanceService.updateList();




        }else {
            substance.setDeleted(false);
            substance = substanceService.save(substance);
            substanceService.updateList();
        }

        return "redirect:" + ExpertiseMgmtUrls.SubstanceList;
    }

    @RequestMapping(ExpertiseMgmtUrls.SubstanceNew)
    public String substanceNew(Model model){
        Substance substance = new Substance();
        model.addAttribute("substance", substance);
        model.addAttribute("substanceType",SubstanceType.getSubstanceTypeList());
        return ExpertiseMgmtTemplates.SubstanceNew;
    }

    @RequestMapping(ExpertiseMgmtUrls.SubstanceView)
    public String getSubstanceViewPage(
            @RequestParam(name = "id") Integer id,
            Model model
    ){
        Substance substance = substanceService.getById(id);
        if (substance==null){
            return "redirect:" + ExpertiseMgmtUrls.SubstanceList;
        }

        model.addAttribute("substance",substance);
        return ExpertiseMgmtTemplates.SubstanceView;
    }

    @RequestMapping(
            value = ExpertiseMgmtUrls.SubstanceDelete,
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public HashMap<String, String> deleteSubstance(
            @RequestParam(name = "id") Integer id

    ){
        User user = userService.getCurrentUserFromContext();
        Substance substance = substanceService.getById(id);
        HashMap<String, String> response = new HashMap<>();
        if (substance!=null && !substance.getDeleted()){
            substanceService.delete(substance, user.getId());
            substanceService.updateList();
            response.put("status", "success");
            response.put("id", String.valueOf(id));
        } else {
            response.put("status", "error");
        }
        return response;
    }
}

