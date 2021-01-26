package uz.maroqand.ecology.cabinet.controller.expertise_mgmt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import uz.maroqand.ecology.cabinet.constant.expertise_mgmt.ExpertiseMgmtTemplates;
import uz.maroqand.ecology.cabinet.constant.expertise_mgmt.ExpertiseMgmtUrls;
import uz.maroqand.ecology.core.component.UserDetailsImpl;
import uz.maroqand.ecology.core.constant.sys.TableHistoryEntity;
import uz.maroqand.ecology.core.constant.sys.TableHistoryType;
import uz.maroqand.ecology.core.entity.expertise.Offer;
import uz.maroqand.ecology.core.entity.sys.File;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.expertise.OfferService;
import uz.maroqand.ecology.core.service.sys.FileService;
import uz.maroqand.ecology.core.service.sys.OrganizationService;
import uz.maroqand.ecology.core.service.sys.TableHistoryService;
import uz.maroqand.ecology.core.service.sys.impl.HelperService;
import uz.maroqand.ecology.core.service.user.UserAdditionalService;
import uz.maroqand.ecology.core.service.user.UserService;

import java.lang.reflect.Type;
import java.util.*;

@Controller
public class OfferController {
    private final OfferService offerService;
    private final UserService userService;
    private final TableHistoryService tableHistoryService;
    private final ObjectMapper objectMapper;
    private final UserAdditionalService userAdditionalService;
    private final FileService fileService;
    private final HelperService helperService;
    private final OrganizationService organizationService;

    @Autowired
    public OfferController(OfferService offerService, UserService userService, TableHistoryService tableHistoryService, ObjectMapper objectMapper, UserAdditionalService userAdditionalService, FileService fileService, HelperService helperService, OrganizationService organizationService){
        this.offerService = offerService;
        this.userService = userService;
        this.tableHistoryService = tableHistoryService;
        this.objectMapper = objectMapper;
        this.userAdditionalService = userAdditionalService;
        this.fileService = fileService;
        this.helperService = helperService;
        this.organizationService = organizationService;
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

        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        List<Offer> offerList = offerPage.getContent();
        List<Object[]> convenientForJSONArray = new ArrayList<>(offerList.size());
        for (Offer offer : offerList){
            System.out.println("##############################");
            System.out.println(offer);
            System.out.println("##############################");
            convenientForJSONArray.add(new Object[]{
                    offer.getId(),
                    offer.getName(),
                    offer.getByudjet(),
                    offer.getFileUzId()!=null?ExpertiseMgmtUrls.OfferFileDownload+"?file_id=" + offer.getFileUzId():"",
                    offer.getFileUzId()!=null?helperService.getFileName(offer.getFileUzId()):"",
                    offer.getFileOzId()!=null?ExpertiseMgmtUrls.OfferFileDownload+"?file_id=" + offer.getFileOzId():"",
                    offer.getFileOzId()!=null?helperService.getFileName(offer.getFileOzId()):"",
                    offer.getFileRuId()!=null?ExpertiseMgmtUrls.OfferFileDownload+"?file_id=" + offer.getFileRuId():"",
                    offer.getFileRuId()!=null?helperService.getFileName(offer.getFileRuId()):"",
                    offer.getOrganizationId()!=null?helperService.getOrganizationName(offer.getOrganizationId(),locale):""
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
        model.addAttribute("organizationList", organizationService.getList());
        model.addAttribute("action_url", ExpertiseMgmtUrls.OfferEdit);
        model.addAttribute("back_url", ExpertiseMgmtUrls.OfferList);
        model.addAttribute("uzOfferList", offerService.getAllByLanguage());
        return ExpertiseMgmtTemplates.OfferNew;
    }

    @RequestMapping(value = ExpertiseMgmtUrls.OfferEdit, method = RequestMethod.POST)
    public String offerEditSubmit(
            Offer offer,
            @RequestParam(name = "byudjet") Integer byudjet
    ){
        User user = userService.getCurrentUserFromContext();
        Offer oldOffer = offerService.getById(offer.getId());
        if(oldOffer == null){
            return "redirect:" + ExpertiseMgmtUrls.OfferList;
        }
        String oldOfferStr = "";
        try {
            oldOfferStr = objectMapper.writeValueAsString(oldOffer);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        if(byudjet==1){
            oldOffer.setTagName("budget_"+organizationService.getById(offer.getOrganizationId()).getRegionId().toString());
//            oldOffer.setTagTitle("offer_description_budget_"+organizationService.getById(offer.getOrganizationId()).getRegionId().toString());
        }else{
            oldOffer.setTagName("farm_"+organizationService.getById(offer.getOrganizationId()).getRegionId().toString());
//            oldOffer.setTagTitle("offer_description_farm_"+organizationService.getById(offer.getOrganizationId()).getRegionId().toString());

        }
        oldOffer.setDeleted(Boolean.FALSE);
        oldOffer.setActive(Boolean.TRUE);
        oldOffer.setName(offer.getName());
        oldOffer.setByudjet(byudjet==1?Boolean.TRUE:Boolean.FALSE);
        oldOffer.setOrganizationId(offer.getOrganizationId());
        oldOffer.setUpdateAt(new Date());
        oldOffer.setUpdateById(user.getId());
//        offerService.complete(offer.getId());
        oldOffer = offerService.save(oldOffer);
        String after = "";
        try {
            after = objectMapper.writeValueAsString(oldOffer);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        tableHistoryService.create(
                TableHistoryType.edit,
                TableHistoryEntity.Offer,
                oldOffer.getId(),
                oldOfferStr,
                after,
                "",
                user.getId(),
                user.getUserAdditionalId()
        );
        return "redirect:" + ExpertiseMgmtUrls.OfferList;
    }


    @RequestMapping(ExpertiseMgmtUrls.OfferNew)
    public String offerNew(){
        User user = userService.getCurrentUserFromContext();
        Offer offer = new Offer();
        offer.setDeleted(Boolean.TRUE);
        offer.setCreatedAt(new Date());
        offer.setCreatedById(user.getId());
        offer = offerService.save(offer);
        String after = "";
        try {
            after = objectMapper.writeValueAsString(offer);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        tableHistoryService.create(
                TableHistoryType.add,
                TableHistoryEntity.Offer,
                offer.getId(),
                null,
                after,
                "",
                user.getId(),
                user.getUserAdditionalId()
        );

        return "redirect:" + ExpertiseMgmtUrls.OfferEdit + "?id=" + offer.getId();
    }

    @RequestMapping(value = ExpertiseMgmtUrls.OfferFileUpload, method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public HashMap<String, Object> uploadFile(
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "fileUz",required = false) MultipartFile fileUz,
            @RequestParam(name = "fileOz", required = false) MultipartFile fileOz,
            @RequestParam(name = "fileRu",required = false) MultipartFile fileRu
    ) {
        User user = userService.getCurrentUserFromContext();

        HashMap<String, Object> responseMap = new HashMap<>();
        responseMap.put("status", 0);
        Offer offer = offerService.getById(id);
        if (offer == null) {
            responseMap.put("message", "Object not found.");
            return responseMap;
        }

        File file=null;
        List<HashMap<String, Object>> filesList = new LinkedList<>();
        HashMap<String, Object> fileMap = new HashMap<>();
        if (fileUz!=null){
            file = fileService.uploadFile(fileUz, user.getId(),"offerUzId="+offer.getId(),"");
            if (file!=null){
                offer.setFileUzId(file.getId());
                offerService.save(offer);
                fileMap.put("name", file.getName());
                fileMap.put("link", ExpertiseMgmtUrls.OfferFileDownload+ "?file_id=" + offer.getFileUzId());
                filesList.add(fileMap);
                responseMap.put("files", filesList);
                responseMap.put("status", 1);
            }
        }else{
            if (fileOz!=null){
                file = fileService.uploadFile(fileOz, user.getId(),"offerOzId="+offer.getId(),"");
                if (file!=null){
                    offer.setFileOzId(file.getId());
                    offerService.save(offer);
                    fileMap.put("name", file.getName());
                    fileMap.put("link", ExpertiseMgmtUrls.OfferFileDownload+ "?file_id=" + offer.getFileOzId());
                    filesList.add(fileMap);
                    responseMap.put("files", filesList);
                    responseMap.put("status", 1);
                }
            }else{
                file = fileService.uploadFile(fileRu, user.getId(),"offerRuId="+offer.getId(),"");
                if (file!=null){
                    offer.setFileRuId(file.getId());
                    offerService.save(offer);
                    fileMap.put("name", file.getName());
                    fileMap.put("link", ExpertiseMgmtUrls.OfferFileDownload+ "?file_id=" + offer.getFileRuId());
                    filesList.add(fileMap);
                    responseMap.put("files", filesList);
                    responseMap.put("status", 1);
                }
            }
        }

        return responseMap;
    }

    @RequestMapping(ExpertiseMgmtUrls.OfferFileDownload)
    @ResponseBody
    public ResponseEntity<Resource> serveFile(
            Authentication authentication,
            @RequestParam(name = "file_id") Integer fileId
    ) {
        if (authentication == null) {
            return null;
        }
        UserDetailsImpl userDetailsAdapter = (UserDetailsImpl)authentication.getPrincipal();
        User user = userDetailsAdapter.getUser();

        File file;
            file = fileService.findByIdAndUploadUserId(fileId, user.getId());
        if (file == null) {
            return null;
        } else {
            return fileService.getFileAsResourceForDownloading(file);
        }
    }

    @RequestMapping(ExpertiseMgmtUrls.OfferView)
    public String getOfferViewPage(
            @RequestParam(name = "id") Integer id,
            Model model
    ){
        Offer offer = offerService.getById(id);
        if (offer==null){
            return "redirect:" + ExpertiseMgmtUrls.ObjectExpertiseList;
        }
        Type type = new TypeToken<List<Offer>>(){}.getType();
        List<HashMap<String,Object>> beforeAndAfterList = tableHistoryService.forAudit(type,TableHistoryEntity.Offer,id);

        model.addAttribute("offer",offer);
        model.addAttribute("beforeAndAfterList",beforeAndAfterList);
        return ExpertiseMgmtTemplates.ActivityView;
    }
}
