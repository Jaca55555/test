package uz.maroqand.ecology.cabinet.controller.mgmt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import uz.maroqand.ecology.cabinet.constant.mgmt.MgmtTemplates;
import uz.maroqand.ecology.cabinet.constant.mgmt.MgmtUrls;
import uz.maroqand.ecology.core.constant.sys.TableHistoryEntity;
import uz.maroqand.ecology.core.constant.sys.TableHistoryType;
import uz.maroqand.ecology.core.entity.user.Position;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.sys.TableHistoryService;
import uz.maroqand.ecology.core.service.user.PositionService;
import uz.maroqand.ecology.core.service.user.UserAdditionalService;
import uz.maroqand.ecology.core.service.user.UserService;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;


/**
 * Created by Utkirbek Boltaev on 29.03.2019.
 * (uz) Tashkiloga tegishli bo'lgan positions
 */
@Controller
public class ExpertisePositionController {

    private final PositionService positionService;
    private final UserService userService;
    private final TableHistoryService tableHistoryService;
    private final ObjectMapper objectMapper;
    private final UserAdditionalService userAdditionalService;

    @Autowired
    public ExpertisePositionController(PositionService positionService, UserService userService, TableHistoryService tableHistoryService, ObjectMapper objectMapper, UserAdditionalService userAdditionalService) {
        this.positionService = positionService;
        this.userService = userService;
        this.tableHistoryService = tableHistoryService;
        this.objectMapper = objectMapper;
        this.userAdditionalService = userAdditionalService;
    }

    @RequestMapping(MgmtUrls.PositionList)
    public String getUserListPage(Model model) {
        User user = userService.getCurrentUserFromContext();
        model.addAttribute("positionList", positionService.getByOrganizationId(user.getOrganizationId()));
        model.addAttribute("create_url", MgmtUrls.PositionCreate);
        model.addAttribute("edit_url", MgmtUrls.PositionUpdate);
        model.addAttribute("get_url", MgmtUrls.PositionGet);
        return MgmtTemplates.PositionList;
    }

    @RequestMapping(MgmtUrls.PositionCreate)
    public String positionCreate(
            @RequestParam(name = "name") String name,
            @RequestParam(name = "nameRu") String nameRu
    ){
        User user = userService.getCurrentUserFromContext();
        Position position = new Position();
        position.setOrganizationId(user.getOrganizationId());
        position.setName(name);
        position.setNameRu(nameRu);
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
        return "redirect:" + MgmtUrls.PositionList;
    }

    @RequestMapping(MgmtUrls.PositionGet)
    @ResponseBody
    public HashMap<String,Object> getPosition(@RequestParam(name = "id",required = true) Integer id){
        HashMap<String,Object> resutl = new HashMap<>();
        Position position = positionService.getById(id);
        resutl.put("id",position.getId());
        resutl.put("name",position.getName());
        resutl.put("nameRu",position.getNameRu());
        return resutl;
    }

    @RequestMapping(MgmtUrls.PositionUpdate)
    public String positionUpdate(
        @RequestParam(name = "id") Integer id,
        @RequestParam(name = "name") String name,
        @RequestParam(name = "nameRu") String nameRu
    ){
        User user = userService.getCurrentUserFromContext();
        Position position = positionService.getById(id, user.getOrganizationId());
        if (position==null){
            return "redirect:" + MgmtUrls.PositionList;
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

        return "redirect:" + MgmtUrls.PositionList;
    }

    @RequestMapping(MgmtUrls.PositionView)
    public String getPositionViewPage(
            @RequestParam(name = "id") Integer positionId,
            Model model
    ){
        User user = userService.getCurrentUserFromContext();
        Position position = positionService.getById(positionId, user.getOrganizationId());
        if (position==null){
            return "redirect:" + MgmtUrls.PositionList;
        }
        Type type = new TypeToken<List<Position>>(){}.getType();
        List<HashMap<String,Object>> beforeAndAfterList = tableHistoryService.forAudit(type,TableHistoryEntity.Position,positionId);

        model.addAttribute("position",position);
        model.addAttribute("beforeAndAfterList",beforeAndAfterList);
        return MgmtTemplates.PositionView;
    }

}
