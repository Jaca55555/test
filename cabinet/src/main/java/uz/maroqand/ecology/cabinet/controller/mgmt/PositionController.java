package uz.maroqand.ecology.cabinet.controller.mgmt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import uz.maroqand.ecology.core.service.user.UserService;

import java.util.HashMap;

@Controller
public class PositionController {

    private final PositionService positionService;
    private final UserService userService;
    private final TableHistoryService tableHistoryService;
    private final ObjectMapper objectMapper;

    @Autowired
    public PositionController(PositionService positionService, UserService userService, TableHistoryService tableHistoryService,ObjectMapper objectMapper) {
        this.positionService = positionService;
        this.userService = userService;
        this.tableHistoryService = tableHistoryService;
        this.objectMapper = objectMapper;
    }

    @RequestMapping(MgmtUrls.PositionList)
    public String getUserListPage(Model model) {
        model.addAttribute("positionList",positionService.getAll());
        model.addAttribute("create_url",MgmtUrls.PositionCreate);
        model.addAttribute("edit_url",MgmtUrls.PositionUpdate);
        model.addAttribute("get_url",MgmtUrls.PositionGet);
        return MgmtTemplates.PositionList;
    }

    @RequestMapping(MgmtUrls.PositionCreate)
    public String positionCreate(
            @RequestParam(name = "name") String name,
            @RequestParam(name = "nameRu") String nameRu
    ){
        User user = userService.getCurrentUserFromContext();
        Position position = new Position();
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
        Position position = positionService.getById(id);
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

}
