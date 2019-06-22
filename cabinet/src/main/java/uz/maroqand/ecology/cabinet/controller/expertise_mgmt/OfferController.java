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
import uz.maroqand.ecology.core.entity.expertise.Offer;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.expertise.OfferService;
import uz.maroqand.ecology.core.service.user.UserService;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Controller
public class OfferController {
    private final OfferService offerService;
    private final UserService userService;

    @Autowired
    public OfferController(OfferService offerService, UserService userService){
        this.offerService = offerService;
        this.userService = userService;
    }

    @RequestMapping(ExpertiseMgmtUrls.OfferList)
    public String offerList(){
        return ExpertiseMgmtTemplates.OfferList;
    }

    @RequestMapping(value = ExpertiseMgmtUrls.OfferListAjax, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public HashMap<String,Object> offerListAjax(Pageable pageable){

        HashMap<String, Object> result = new HashMap<>();
        Page<Offer> offerPage = offerService.getAll(pageable);
        result.put("recordsTotal", offerPage.getTotalElements()); //Total elements
        result.put("recordsFiltered", offerPage.getTotalElements()); //Filtered elements

        List<Offer> offerList = offerPage.getContent();
        List<Object[]> convenientForJSONArray = new ArrayList<>(offerList.size());
        for (Offer offer : offerList){
            convenientForJSONArray.add(new Object[]{
                    offer.getId(),
                    offer.getName(),
                    offer.getLanguage(),
                    offer.getDescription(),
                    offer.getMainId()
            });
        }
        result.put("data",convenientForJSONArray);
        return result;
    }

    @RequestMapping(ExpertiseMgmtUrls.OfferEdit)
    public String offerEdit(
            @RequestParam(name = "id") Integer id,
            Model model
    ){
        Offer offer = offerService.getById(id);
        if(offer == null){
            return "redirect:" + ExpertiseMgmtUrls.OfferList;
        }

        model.addAttribute("offer", offer);
        model.addAttribute("action_url", ExpertiseMgmtUrls.OfferEdit);
        model.addAttribute("uzOfferList", offerService.getAllByLanguage("uz"));
        return ExpertiseMgmtTemplates.OfferNew;
    }

    @RequestMapping(value = ExpertiseMgmtUrls.OfferEdit, method = RequestMethod.POST)
    public String offerEditSubmit(Offer offer){
        User user = userService.getCurrentUserFromContext();
        Offer oldOffer = offerService.getById(offer.getId());
        if(oldOffer == null){
            return "redirect:" + ExpertiseMgmtUrls.OfferList;
        }

        oldOffer.setName(offer.getName());
        oldOffer.setLanguage(offer.getLanguage());
        oldOffer.setDescription(offer.getDescription());
        oldOffer.setMainId(offer.getMainId());
        oldOffer.setUpdateAt(new Date());
        oldOffer.setUpdateById(user.getId());
        offerService.save(oldOffer);
        return "redirect:" + ExpertiseMgmtUrls.OfferList;
    }


    @RequestMapping(ExpertiseMgmtUrls.OfferNew)
    public String offerNew(Model model){

        Offer offer = new Offer();
        model.addAttribute("offer", offer);
        model.addAttribute("action_url", ExpertiseMgmtUrls.OfferNew);
        model.addAttribute("uzOfferList", offerService.getAllByLanguage("uz"));
        return ExpertiseMgmtTemplates.OfferNew;
    }

    @RequestMapping(value = ExpertiseMgmtUrls.OfferNew, method = RequestMethod.POST)
    public String offerNewSubmit(Offer offer){
        User user = userService.getCurrentUserFromContext();

        offer.setActive(true);
        offer.setDeleted(false);
        offer.setCreatedAt(new Date());
        offer.setCreatedById(user.getId());
        offerService.save(offer);
        return "redirect:" + ExpertiseMgmtUrls.OfferList;
    }
}
