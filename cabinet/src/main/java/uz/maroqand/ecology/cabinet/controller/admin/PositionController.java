package uz.maroqand.ecology.cabinet.controller.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import uz.maroqand.ecology.cabinet.constant.admin.AdminTemplates;
import uz.maroqand.ecology.cabinet.constant.admin.AdminUrls;
import uz.maroqand.ecology.core.constant.sys.TableHistoryEntity;
import uz.maroqand.ecology.core.constant.sys.TableHistoryType;
import uz.maroqand.ecology.core.entity.user.Position;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.sys.OrganizationService;
import uz.maroqand.ecology.core.service.sys.TableHistoryService;
import uz.maroqand.ecology.core.service.user.PositionService;
import uz.maroqand.ecology.core.service.user.UserAdditionalService;
import uz.maroqand.ecology.core.service.user.UserService;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Utkirbek Boltaev on 29.03.2019.
 * (uz) All positions
 */
@Controller
public class PositionController {

    private final PositionService positionService;
    private final UserService userService;
    private final TableHistoryService tableHistoryService;
    private final ObjectMapper objectMapper;
    private final UserAdditionalService userAdditionalService;
    private final OrganizationService organizationService;

    @Autowired
    public PositionController(PositionService positionService, UserService userService, TableHistoryService tableHistoryService, ObjectMapper objectMapper, UserAdditionalService userAdditionalService, OrganizationService organizationService) {
        this.positionService = positionService;
        this.userService = userService;
        this.tableHistoryService = tableHistoryService;
        this.objectMapper = objectMapper;
        this.userAdditionalService = userAdditionalService;
        this.organizationService = organizationService;
    }

    @RequestMapping(AdminUrls.PositionList)
    public String getUserListPage(Model model) {
        model.addAttribute("positionList", positionService.getAll());
        model.addAttribute("organizationList", organizationService.getList());
        model.addAttribute("create_url", AdminUrls.PositionCreate);
        model.addAttribute("edit_url", AdminUrls.PositionUpdate);
        model.addAttribute("get_url", AdminUrls.PositionGet);
        return AdminTemplates.PositionList;
    }

    @RequestMapping(AdminUrls.PositionCreate)
    public String positionCreate(
            @RequestParam(name = "name") String name,
            @RequestParam(name = "nameRu") String nameRu,
            @RequestParam(name = "organizationId") Integer organizationId,
            @RequestParam(name = "docIndex") String docIndex
    ){
        User user = userService.getCurrentUserFromContext();
        Position position = new Position();
        position.setName(name);
        position.setNameRu(nameRu);
        position.setOrganizationId(organizationId);
        position.setDocIndex(docIndex);
        position.setDeleted(Boolean.FALSE);
        position = positionService.save(position);
        String after = "";
        try {
            after = objectMapper.writeValueAsString(position);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        tableHistoryService.create(
          TableHistoryType.add,
          TableHistoryEntity.Position,
          position.getId(),
          null,
                after,
          "",
          user.getId(),
          user.getUserAdditionalId()
        );
        return "redirect:" + AdminUrls.PositionList;
    }

    @RequestMapping(AdminUrls.PositionGet)
    @ResponseBody
    public HashMap<String,Object> getPosition(@RequestParam(name = "id",required = true) Integer id){
        HashMap<String,Object> result = new HashMap<>();
        Position position = positionService.getById(id);
        result.put("id",position.getId());
        result.put("name",position.getName());
        result.put("nameRu",position.getNameRu());
        return result;
    }

    @RequestMapping(AdminUrls.PositionUpdate)
    public String positionUpdate(
        @RequestParam(name = "id") Integer id,
        @RequestParam(name = "name") String name,
        @RequestParam(name = "nameRu") String nameRu,
        @RequestParam(name = "organizationId") Integer organizationId,
        @RequestParam(name = "docIndex") String docIndex
    ){
        User user = userService.getCurrentUserFromContext();
        Position position = positionService.getById(id);
        if (position==null){
            return "redirect:" + AdminUrls.PositionList;
        }
        String oldPosition="";
        String after = "";
        try {
            oldPosition = objectMapper.writeValueAsString(position);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        position.setName(name);
        position.setNameRu(nameRu);
        position.setOrganizationId(organizationId);
        position.setDocIndex(docIndex);
        position = positionService.save(position);
        try {
            after = objectMapper.writeValueAsString(position);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        tableHistoryService.create(
                TableHistoryType.edit,
                TableHistoryEntity.Position,
                position.getId(),
                oldPosition,
                after,
                "",
                user.getId(),
                user.getUserAdditionalId()
        );

        return "redirect:" + AdminUrls.PositionList;
    }

    @RequestMapping(AdminUrls.PositionView)
    public String getPositionViewPage(
            @RequestParam(name = "id") Integer positionId,
            Model model
    ){
        Position position = positionService.getById(positionId);
        if (position==null){
            return "redirect:" + AdminUrls.PositionList;
        }
        Type type = new TypeToken<List<Position>>(){}.getType();
        List<HashMap<String,Object>> beforeAndAfterList = tableHistoryService.forAudit(type,TableHistoryEntity.Position,positionId);

        model.addAttribute("position",position);
        model.addAttribute("beforeAndAfterList",beforeAndAfterList);
        return AdminTemplates.PositionView;
    }

}
