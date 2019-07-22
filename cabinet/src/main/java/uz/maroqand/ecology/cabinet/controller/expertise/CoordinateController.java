package uz.maroqand.ecology.cabinet.controller.expertise;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import uz.maroqand.ecology.cabinet.constant.expertise.ExpertiseTemplates;
import uz.maroqand.ecology.cabinet.constant.expertise.ExpertiseUrls;
import uz.maroqand.ecology.core.entity.client.Client;
import uz.maroqand.ecology.core.entity.expertise.Coordinate;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.expertise.CoordinateService;
import uz.maroqand.ecology.core.service.sys.SoatoService;
import uz.maroqand.ecology.core.service.sys.impl.HelperService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.core.util.DateParser;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Controller
public class CoordinateController {

    private final UserService userService;
    private final CoordinateService coordinateService;
    private final HelperService helperService;
    private final SoatoService soatoService;

    @Autowired
    public CoordinateController(UserService userService, CoordinateService coordinateService, HelperService helperService, SoatoService soatoService){
        this.userService = userService;
        this.coordinateService = coordinateService;
        this.helperService = helperService;
        this.soatoService = soatoService;
    }

    @RequestMapping(ExpertiseUrls.CoordinateList)
    public String coordinateList(Model model){
        model.addAttribute("regionsList", soatoService.getRegions());
        model.addAttribute("subRegionsList", soatoService.getSubRegions());
        return ExpertiseTemplates.CoordinateList;
    }

    @RequestMapping(value = ExpertiseUrls.CoordinateListAjax, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public HashMap<String,Object> coordinateListAjax(
            @RequestParam(name = "id", required = false) Integer id,
            @RequestParam(name = "tin", required = false) Integer tin,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "number", required = false) String number,
            @RequestParam(name = "regionId", required = false) Integer regionId,
            @RequestParam(name = "subRegionId", required = false) Integer subRegionId,
            @RequestParam(name = "dateBegin", required = false) String dateBeginStr,
            @RequestParam(name = "dateEnd", required = false) String dateEndStr,
            Pageable pageable
    ){
        name = StringUtils.trimToNull(name);
        number = StringUtils.trimToNull(number);
        dateBeginStr = StringUtils.trimToNull(dateBeginStr);
        dateEndStr = StringUtils.trimToNull(dateEndStr);

        Date dateBegin = DateParser.TryParse(dateBeginStr, Common.uzbekistanDateFormat);
        Date dateEnd = DateParser.TryParse(dateEndStr, Common.uzbekistanDateFormat);

        User user = userService.getCurrentUserFromContext();
        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        HashMap<String, Object> result = new HashMap<>();

        Page<Coordinate> coordinatePage = coordinateService.findFiltered(id, tin, name, number ,regionId, subRegionId, dateBegin, dateEnd, pageable);

        List<Coordinate> coordinateList = coordinatePage.getContent();
        List<Object[]> convenientForJSONArray = new ArrayList<>(coordinateList.size());
        for (Coordinate coordinate : coordinateList){
            Client client = coordinate.getClient();
            convenientForJSONArray.add(new Object[]{
                    coordinate.getId(),
                    coordinate.getNumber(),
                    coordinate.getDate() != null ? Common.uzbekistanDateAndTimeFormat.format(coordinate.getDate()) : "",
                    client.getTin(),
                    client.getName(),
                    coordinate.getRegionId() != null ? helperService.getSoatoName(coordinate.getRegionId(),locale) : "",
                    coordinate.getSubRegionId() != null ? helperService.getSoatoName(coordinate.getSubRegionId(),locale) : "",
            });
        }

        result.put("recordsTotal", coordinatePage.getTotalElements()); //Total elements
        result.put("recordsFiltered", coordinatePage.getTotalElements()); //Filtered elements
        result.put("data",convenientForJSONArray);
        return result;
    }
}
