package uz.maroqand.ecology.cabinet.controller.admin;

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
import uz.maroqand.ecology.cabinet.constant.mgmt.MgmtTemplates;
import uz.maroqand.ecology.cabinet.constant.mgmt.MgmtUrls;
import uz.maroqand.ecology.core.constant.sys.TableHistoryEntity;
import uz.maroqand.ecology.core.constant.sys.TableHistoryType;
import uz.maroqand.ecology.core.entity.billing.MinWage;
import uz.maroqand.ecology.core.entity.sys.TableHistory;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.billing.MinWageService;
import uz.maroqand.ecology.core.service.sys.TableHistoryService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.core.util.DateParser;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Controller
public class MinWageController {

    private final MinWageService minWageService;
    private final TableHistoryService tableHistoryService;
    private final ObjectMapper objectMapper;
    private final UserService userService;

    @Autowired
    public MinWageController(MinWageService minWageService, TableHistoryService tableHistoryService, ObjectMapper objectMapper, UserService userService) {
        this.minWageService = minWageService;
        this.tableHistoryService = tableHistoryService;
        this.objectMapper = objectMapper;
        this.userService = userService;
    }

    @RequestMapping(MgmtUrls.MinWageList)
    public String getMinWageListPage(){
        return MgmtTemplates.MinWageList;
    }

    @RequestMapping(value = MgmtUrls.MinWageListAjax,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public HashMap<String,Object> getMinWageListAjaxPage(
            @RequestParam(name = "id",required = false)Integer id,
            @RequestParam(name = "amount",required = false)Double amount,
            @RequestParam(name = "beginDate",required = false)String beginDate,
            @RequestParam(name = "registeredAt",required = false)String regDate,
            Pageable pageable
    ){
        HashMap<String,Object> result = new HashMap<>();
        Date beginDateMinWage = DateParser.TryParse(beginDate,Common.uzbekistanDateFormat);
        Date regDateMinWage = DateParser.TryParse(regDate,Common.uzbekistanDateFormat);

        Page<MinWage> minWagePage = minWageService.findFiltered(id,pageable);

        result.put("recordsTotal", minWagePage.getTotalElements()); //Total elements
        result.put("recordsFiltered", minWagePage.getTotalElements()); //Filtered elements

        List<MinWage> minWageList = minWagePage.getContent();
        List<Object[]> convenientForJSONArray = new ArrayList<>(minWageList.size());
        for (MinWage minWage : minWageList){
            convenientForJSONArray.add(new Object[]{
                    minWage.getId()!=null?minWage.getId():"",
                    minWage.getAmount()!=null?minWage.getAmount():"",
                    minWage.getBeginDate()!=null?Common.uzbekistanDateFormat.format(minWage.getBeginDate()):"",
                    minWage.getRegisteredAt()!=null?Common.uzbekistanDateFormat.format(minWage.getRegisteredAt()):"",
            });
        }
        result.put("data",convenientForJSONArray);
        return result;

    }

    @RequestMapping(MgmtUrls.MinWageNew)
    public String getMinWageNewPage(Model model){
        model.addAttribute("minWage",new MinWage());
        model.addAttribute("action_url",MgmtUrls.MinWageCreate);
        return MgmtTemplates.MinWageNew;
    }

    @RequestMapping(value = MgmtUrls.MinWageCreate,method = RequestMethod.POST)
    public String createMinWage(
            @RequestParam(name = "beginDate",required = false)String beginDate,
            MinWage minWage
    ){

        System.out.println("beginDate == " + beginDate);
        User user = userService.getCurrentUserFromContext();
        Date beginDateMinWage = DateParser.TryParse(beginDate,Common.uzbekistanDateFormat);
        String after = "";
        try {
            after = objectMapper.writeValueAsString(minWage);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        minWage.setBeginDate(beginDateMinWage);
        minWage.setRegisteredAt(new Date());
        minWage.setDeleted(false);
        minWageService.createMinWage(minWage);
        tableHistoryService.create(
                TableHistoryType.add,
                TableHistoryEntity.MinWage,
                minWage.getId(),
                "",
                after,
                "MinWage successfully created!!!",
                user.getId(),
                user.getUserAdditionalId()
        );
        return "redirect:" + MgmtUrls.MinWageList;
    }
    @RequestMapping(MgmtUrls.MinWageEdit)
    public String getMinWageNewPage(
            @RequestParam(name = "id")Integer id,
            Model model
    ){
        MinWage minWage = minWageService.getById(id);
        if (minWage == null){
            return "redirect:" + MgmtUrls.MinWageList;
        }
        model.addAttribute("minWage",minWage);
        model.addAttribute("action_url",MgmtUrls.MinWageUpdate);
        return MgmtTemplates.MinWageNew;
    }

    @RequestMapping(value = MgmtUrls.MinWageUpdate, method = RequestMethod.POST)
    public String updateMinWage(
            @RequestParam(name = "id")Integer id,
            @RequestParam(name = "beginDate",required = false)String beginDate,
            MinWage minWage
    ){
        User user = userService.getCurrentUserFromContext();
        Date beginDateMinWage = DateParser.TryParse(beginDate,Common.uzbekistanDateFormat);
        String before = "";
        String after = "";
        MinWage oldMinWage = minWageService.getById(id);
        if (oldMinWage == null){
            return "redirect:" + MgmtUrls.MinWageList;
        }
        try {
            before = objectMapper.writeValueAsString(oldMinWage);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        oldMinWage.setBeginDate(beginDateMinWage);
        oldMinWage.setRegisteredAt(new Date());
        oldMinWage.setAmount(minWage.getAmount());
        minWageService.updateMinWage(oldMinWage);
        try {
            after = objectMapper.writeValueAsString(oldMinWage);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        tableHistoryService.create(
                TableHistoryType.edit,
                TableHistoryEntity.MinWage,
                oldMinWage.getId(),
                before,
                after,
                "MinWage successfully updated!!!",
                user.getId(),
                user.getUserAdditionalId()
        );
        return "redirect:" + MgmtUrls.MinWageList;
    }

    @RequestMapping(value = MgmtUrls.MinWageDelete, method = RequestMethod.POST)
    @ResponseBody
    public HashMap<String, Object> deleteWage(
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "msg")String msg) {
        User user = userService.getCurrentUserFromContext();
        HashMap<String,Object> response = new HashMap<>();
        MinWage wage = minWageService.getById(id);
        if (wage != null) {
            minWageService.delete(wage, user.getId(), msg);
            response.put("status", "success");
        } else {
            response.put("status", "error");
        }
        return response;
    }
}
