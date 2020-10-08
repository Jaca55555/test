package uz.maroqand.ecology.cabinet.controller.expertise;

import org.apache.commons.lang3.StringUtils;
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
import uz.maroqand.ecology.cabinet.constant.expertise.ExpertiseTemplates;
import uz.maroqand.ecology.cabinet.constant.expertise.ExpertiseUrls;
import uz.maroqand.ecology.cabinet.constant.expertise_mgmt.ExpertiseMgmtUrls;
import uz.maroqand.ecology.core.component.UserDetailsImpl;
import uz.maroqand.ecology.core.constant.expertise.ConclusionStatus;
import uz.maroqand.ecology.core.constant.expertise.LogStatus;
import uz.maroqand.ecology.core.constant.expertise.RegApplicationStatus;
import uz.maroqand.ecology.core.dto.expertise.FilterDto;
import uz.maroqand.ecology.core.entity.client.Client;
import uz.maroqand.ecology.core.entity.expertise.*;
import uz.maroqand.ecology.core.entity.sys.File;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.client.ClientService;
import uz.maroqand.ecology.core.service.expertise.*;
import uz.maroqand.ecology.core.service.sys.DocumentRepoService;
import uz.maroqand.ecology.core.service.sys.FileService;
import uz.maroqand.ecology.core.service.sys.SoatoService;
import uz.maroqand.ecology.core.service.sys.impl.HelperService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.core.util.DateParser;
import uz.maroqand.ecology.core.util.TinParser;

import java.util.*;

@Controller
public class ConclusionController {

    private final UserService userService;
    private final RegApplicationService regApplicationService;
    private final ClientService clientService;
    private final ConclusionService conclusionService;
    private final RegApplicationLogService regApplicationLogService;
    private final HelperService helperService;
    private final DocumentRepoService documentRepoService;
    private final SoatoService soatoService;
    private final ObjectExpertiseService objectExpertiseService;
    private final ActivityService activityService;
    private final FileService fileService;

    @Autowired
    public ConclusionController(UserService userService, RegApplicationService regApplicationService, ClientService clientService, ConclusionService conclusionService, RegApplicationLogService regApplicationLogService, HelperService helperService, DocumentRepoService documentRepoService, SoatoService soatoService, ObjectExpertiseService objectExpertiseService, ActivityService activityService, FileService fileService) {
        this.userService = userService;
        this.regApplicationService = regApplicationService;
        this.clientService = clientService;
        this.conclusionService = conclusionService;
        this.regApplicationLogService = regApplicationLogService;
        this.helperService = helperService;
        this.documentRepoService = documentRepoService;
        this.soatoService = soatoService;
        this.objectExpertiseService = objectExpertiseService;
        this.activityService = activityService;
        this.fileService = fileService;
    }


    @RequestMapping(ExpertiseUrls.ConclusionList)
    public String conclusionList(Model model){
        return ExpertiseTemplates.ConclusionList;
    }

    @RequestMapping(value = ExpertiseUrls.ConclusionListAjax, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public HashMap<String,Object> conclusionListAjax(
            @RequestParam(name = "id", required = false) Integer id,
            @RequestParam(name = "dateBegin", required = false) String dateBeginStr,
            @RequestParam(name = "dateEnd", required = false) String dateEndStr,
            @RequestParam(name = "tin", required = false) String tinStr,
            @RequestParam(name = "name", required = false) String name,
            Pageable pageable
    ){

        Integer tin = TinParser.trimIndividualsTinToNull(tinStr);
        name = StringUtils.trimToNull(name);
        User user = userService.getCurrentUserFromContext();
        Date dateBegin = DateParser.TryParse(dateBeginStr, Common.uzbekistanDateFormat);
        Date dateEnd = DateParser.TryParse(dateEndStr, Common.uzbekistanDateFormat);
        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        HashMap<String, Object> result = new HashMap<>();
        Page<Conclusion> conclusionPage = conclusionService.findFiltered(userService.isAdmin()?null:user.getOrganizationId(),id, dateBegin, dateEnd,tin,name, pageable);
        Calendar c = Calendar.getInstance();
        c.set(c.getTime().getYear(),c.getTime().getMonth(),c.getTime().getDate(),0,0,0);
        List<Conclusion> conclusionList = conclusionPage.getContent();
        List<Object[]> convenientForJSONArray = new ArrayList<>(conclusionList.size());
        for (Conclusion conclusion : conclusionList){
            RegApplication regApplication = null;
            if (conclusion.getRegApplicationId()!=null){
                regApplication = regApplicationService.getById(conclusion.getRegApplicationId());
            }
            convenientForJSONArray.add(new Object[]{
                    conclusion.getId(),
                    conclusion.getNumber(),
                    conclusion.getDate()!= null ? Common.uzbekistanDateAndTimeFormat.format(conclusion.getDate()) : "",
                    regApplication!=null && regApplication.getApplicant()!=null?regApplication.getApplicant().getTin():"",
                    regApplication!=null ? regApplication.getName():"",
                    regApplication!=null && regApplication.getMaterials()!=null? regApplication.getMaterials():"",
                    regApplication!=null && regApplication.getCategory() !=null ? helperService.getTranslation(regApplication.getCategory().getName(),locale) : "",
                    conclusion.getDeadlineDate() != null ? Common.uzbekistanDateAndTimeFormat.format(conclusion.getDeadlineDate()) : "",
                    conclusion.getDeadlineDate() != null ? (conclusion.getDeadlineDate().compareTo(c.getTime())>=0?Boolean.TRUE:Boolean.FALSE): Boolean.TRUE,
                    regApplication!=null && regApplication.getApplicant()!=null?regApplication.getApplicant().getName():""
            });
        }

        result.put("recordsTotal", conclusionPage.getTotalElements()); //Total elements
        result.put("recordsFiltered", conclusionPage.getTotalElements()); //Filtered elements
        result.put("data",convenientForJSONArray);
        return result;
    }

    @RequestMapping(ExpertiseUrls.ConclusionView)
    public String conclusionView(
            @RequestParam(name = "id") Integer id,
            Model model
    ){
        Conclusion conclusion = conclusionService.getById(id);
        String locale = LocaleContextHolder.getLocale().toLanguageTag();

        if(conclusion == null || conclusion.getStatus().equals(ConclusionStatus.Initial)){
            return "redirect:" + ExpertiseUrls.ConclusionList;
        }
        RegApplication regApplication = null;
        if(conclusion.getRegApplicationId() != null){
            regApplication = regApplicationService.getById(conclusion.getRegApplicationId());
        }
        if(conclusion.getDocumentRepoId() != null){
            model.addAttribute("documentRepo", documentRepoService.getDocument(conclusion.getDocumentRepoId()));
        }
        model.addAttribute("conclusion", conclusion);
        model.addAttribute("regApplication", regApplication);

        return ExpertiseTemplates.ConclusionView;
    }
    @RequestMapping(value = ExpertiseUrls.ConclusionNewList,method = RequestMethod.GET)
    public String getConclusionNewList(Model model){
        List<LogStatus> logStatusList = new ArrayList<>();
        logStatusList.add(LogStatus.Initial);
        logStatusList.add(LogStatus.Approved);
        logStatusList.add(LogStatus.Denied);

        Conclusion conclusion = new Conclusion();

        model.addAttribute("regions",soatoService.getRegions());
        model.addAttribute("subRegions",soatoService.getSubRegions());
        model.addAttribute("objectExpertiseList",objectExpertiseService.getList());
        model.addAttribute("activityList",activityService.getList());
        model.addAttribute("conclusion", conclusion);

        return ExpertiseTemplates.ConclusionNewList;
    }

    @RequestMapping(value = ExpertiseUrls.ConclusionNewList,method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public HashMap<String,Object> getConclusionNewListAjax(
            FilterDto filterDto,
            Pageable pageable
    ){
        User user = userService.getCurrentUserFromContext();
        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        HashMap<String, Object> result = new HashMap<>();

        Page<RegApplication> regApplicationPage = regApplicationService.findFiltered(
                filterDto,
                user.getOrganizationId(),
                null,
                null,
                null,
                null,
                pageable
        );

        List<RegApplication> regApplicationList = regApplicationPage.getContent();
        List<Object[]> convenientForJSONArray = new ArrayList<>(regApplicationList.size());
        for (RegApplication regApplication : regApplicationList){
            Client client = regApplication.getApplicant();
            Boolean isfileUpload = false;// agar status completed bo'lmasa
            if (regApplication.getStatus()==null || regApplication.getStatus().equals(RegApplicationStatus.Initial)){
                isfileUpload = true;
            }
            convenientForJSONArray.add(new Object[]{
                    regApplication.getId(),
                    client != null ? client.getTin() : "",
                    client != null ? client.getName() : "",
                    client != null ? helperService.getApplicantType(client.getType().getId(),locale):"",
                    client != null ? client.getOpfId()!=null? helperService.getOpfName(client.getOpfId(),locale):"" : "",
                    client != null ? client.getOked() : "",
                    client != null ? client.getRegionId()!=null?helperService.getSoatoName(client.getRegionId(),locale): "" : "",
                    client != null ? client.getSubRegionId()!=null?helperService.getSoatoName(client.getSubRegionId(),locale) : "" : "",
                    regApplication.getCreatedAt()!=null?Common.uzbekistanDateAndTimeFormat.format(regApplication.getCreatedAt()):"",
                    isfileUpload
            });
        }

        result.put("recordsTotal", regApplicationPage.getTotalElements()); //Total elements
        result.put("recordsFiltered", regApplicationPage.getTotalElements()); //Filtered elements
        result.put("data",convenientForJSONArray);
        return result;
    }

    @RequestMapping(value = ExpertiseUrls.ConclusionFileUpload, method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public HashMap<String, File> fileUpload(@RequestParam(name = "file") MultipartFile file){
        File file_ = fileService.uploadFile(file, userService.getCurrentUserFromContext().getId(), file.getOriginalFilename(), file.getContentType());
        HashMap<String, File> res = new HashMap<>();
        res.put("data", file_);

        return res;
    }

    @RequestMapping(ExpertiseUrls.ConclusionFileDownload)
    @ResponseBody
    public ResponseEntity<Resource> serveFile(
            @RequestParam(name = "id") Integer fileId
    ) {

        File file = fileService.findById(fileId);
        if (file == null) {
            return null;
        } else {
            return fileService.getFileAsResourceForDownloading(file);
        }
    }

    @RequestMapping(ExpertiseUrls.ConclusionFileDownloadForView)
    @ResponseBody
    public ResponseEntity<Resource> conclusionFileDownloadForView(
            @RequestParam(name = "id") Integer conclusionId
    ) {

        Conclusion conclusion = conclusionService.getById(conclusionId);
        if (conclusion==null || conclusion.getConclusionFileId()==null){
            return null;
        }

        File file = fileService.findById(conclusion.getConclusionFileId());
        if (file == null) {
            return null;
        } else {
            return fileService.getFileAsResourceForDownloading(file);
        }
    }

    @RequestMapping(ExpertiseUrls.ConclusionFileAdd)
    public String conclusionFileAdd(
            @RequestParam(name = "id") Integer regApplicationId,
            @RequestParam(name = "file_id") Integer fileId
    ){
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(regApplicationId);
        if (regApplication==null){
            return "redirect:" + ExpertiseUrls.ConclusionList;
        }

        Conclusion conclusion = new Conclusion();
        conclusion.setRegApplicationId(regApplicationId);
        conclusion.setStatus(ConclusionStatus.Active);
        conclusion.setConclusionFileId(fileId);
        conclusion.setUploadedUser(user.getId());
        conclusion.setDeleted(Boolean.FALSE);
        conclusion = conclusionService.save(conclusion);

        regApplication.setConclusionId(conclusion.getId());
        regApplicationService.update(regApplication);
        return "redirect:" + ExpertiseUrls.ConclusionList;

    }
}
