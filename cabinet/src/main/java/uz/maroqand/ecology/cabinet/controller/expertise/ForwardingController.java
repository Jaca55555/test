package uz.maroqand.ecology.cabinet.controller.expertise;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import uz.maroqand.ecology.cabinet.constant.expertise.ExpertiseTemplates;
import uz.maroqand.ecology.cabinet.constant.expertise.ExpertiseUrls;
import uz.maroqand.ecology.core.constant.expertise.ForwardingStatus;
import uz.maroqand.ecology.core.entity.billing.Invoice;
import uz.maroqand.ecology.core.entity.billing.Payment;
import uz.maroqand.ecology.core.entity.client.Client;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;
import uz.maroqand.ecology.core.entity.sys.File;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.billing.InvoiceService;
import uz.maroqand.ecology.core.service.billing.PaymentService;
import uz.maroqand.ecology.core.service.client.ClientService;
import uz.maroqand.ecology.core.service.expertise.RegApplicationService;
import uz.maroqand.ecology.core.service.sys.FileService;
import uz.maroqand.ecology.core.service.sys.SoatoService;
import uz.maroqand.ecology.core.service.sys.impl.HelperService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.core.util.DateParser;

import java.util.*;

/**
 * Created by Utkirbek Boltaev on 15.06.2019.
 * (uz)
 * (ru)
 */
@Controller
public class ForwardingController {

    private final RegApplicationService regApplicationService;
    private final ClientService clientService;
    private final UserService userService;
    private final HelperService helperService;
    private final SoatoService soatoService;
    private final InvoiceService invoiceService;
    private final FileService fileService;

    @Autowired
    public ForwardingController(
            RegApplicationService regApplicationService,
            ClientService clientService,
            UserService userService,
            HelperService helperService,
            SoatoService soatoService,
            InvoiceService invoiceService,
            FileService fileService) {
        this.regApplicationService = regApplicationService;
        this.clientService = clientService;
        this.userService = userService;
        this.helperService = helperService;
        this.soatoService = soatoService;
        this.invoiceService = invoiceService;
        this.fileService = fileService;
    }

    @RequestMapping(ExpertiseUrls.ForwardingList)
    public String getForwardingListPage(Model model){
        model.addAttribute("regions",soatoService.getRegions());
        model.addAttribute("subRegions",soatoService.getSubRegions());
        model.addAttribute("statusList", ForwardingStatus.getForwardingStatusList());
        return ExpertiseTemplates.ForwardingList;
    }

    @RequestMapping(value = ExpertiseUrls.ForwardingListAjax,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public HashMap<String,Object> getForwardingListAjaxPage(
            @RequestParam(name = "tin",required = false)Integer tin,
            @RequestParam(name = "name",required = false)String name,
            @RequestParam(name = "regApplicationId",required = false)Integer regApplicationId,
            @RequestParam(name = "status",required = false)Integer status,
            @RequestParam(name = "regionId",required = false)Integer regionId,
            @RequestParam(name = "subRegionId",required = false)Integer subRegionId,
            @RequestParam(name = "regDateBegin",required = false)String registrationDateBegin,
            @RequestParam(name = "regDateEnd",required = false)String registrationDateEnd,
            @RequestParam(name = "contractDateBegin",required = false)String contractDateBeginDate,
            @RequestParam(name = "contractDateEnd",required = false)String contractDateEndDate,
            Pageable pageable
    ){
        HashMap<String,Object> result = new HashMap<>();
        User user = userService.getCurrentUserFromContext();
        String locale = LocaleContextHolder.getLocale().toLanguageTag();

        Date regDateBegin = DateParser.TryParse(registrationDateBegin, Common.uzbekistanDateFormat);
        Date regDateEnd = DateParser.TryParse(registrationDateEnd,Common.uzbekistanDateFormat);
        Date contractDateBegin = DateParser.TryParse(contractDateBeginDate,Common.uzbekistanDateFormat);
        Date contractDateEnd = DateParser.TryParse(contractDateEndDate,Common.uzbekistanDateFormat);

        Page<RegApplication> regApplicationPage = regApplicationService.findFiltered(user.getId(),pageable);

        result.put("recordsTotal", regApplicationPage.getTotalElements()); //Total elements
        result.put("recordsFiltered", regApplicationPage.getTotalElements()); //Filtered elements

        List<RegApplication> regApplicationList = regApplicationPage.getContent();
        List<Object[]> convenientForJSONArray = new ArrayList<>(regApplicationList.size());
        for (RegApplication regApplication : regApplicationList){
            Client client = clientService.getById(regApplication.getApplicantId());
            Invoice invoice = invoiceService.getInvoice(regApplication.getInvoiceId());
            convenientForJSONArray.add(new Object[]{
                    regApplication.getId(),
                    client.getTin(),
                    client.getName(),
                    regApplication.getMaterialId() != null ?helperService.getMaterial(regApplication.getMaterialId(),locale):"",
                    regApplication.getActivityId() != null ?helperService.getActivity(regApplication.getActivityId(),locale):"",
                    regApplication.getCreatedAt() != null ?Common.uzbekistanDateFormat.format(regApplication.getCreatedAt()):"",
                    invoice.getRegisteredAt()!=null ?Common.uzbekistanDateFormat.format(setExecutionDate(invoice.getRegisteredAt(),regApplication.getDeadline())):"",
                    regApplication.getForwardingStatus() != null ?regApplication.getForwardingStatus().getName():""
            });
        }
        result.put("data",convenientForJSONArray);
        return result;
    }

    @RequestMapping(ExpertiseUrls.ForwardingChecking)
    public String getCheckingPage(
            @RequestParam(name = "id")Integer regApplicationId,
            Model model
    ) {
        RegApplication regApplication = regApplicationService.getById(regApplicationId);
        if (regApplication == null){
            return "redirect:" + ExpertiseUrls.ForwardingList;
        }

        Client client = clientService.getById(regApplication.getApplicantId());
        Invoice invoice = invoiceService.getInvoice(regApplication.getInvoiceId());
        User user = userService.findById(regApplication.getPerformerId());
        model.addAttribute("performer",user);
        model.addAttribute("applicant",client);
        model.addAttribute("regApplication",regApplication);
        model.addAttribute("invoice",invoice);
        model.addAttribute("cancel_url",ExpertiseUrls.ForwardingList);
        return ExpertiseTemplates.ForwardingChecking;
    }

    @RequestMapping(value = ExpertiseUrls.ForwardingFileUpload, method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public HashMap<String, Object> uploadFile(
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "file_name") String fileName,
            @RequestParam(name = "file") MultipartFile multipartFile
    ) {
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication  = regApplicationService.getById(id);

        HashMap<String, Object> responseMap = new HashMap<>();
        responseMap.put("status", 0);

        if (regApplication == null) {
            responseMap.put("message", "Object not found.");
            return responseMap;
        }
        /*if (regApplication.getStatus() != regApplication.Initial) {
            responseMap.put("message", "Object will not able to update.");
            return responseMap;
        }*/

        File file = fileService.uploadFile(multipartFile, user.getId(),"regApplication="+regApplication.getId(),fileName);
        if (file != null) {
            Set<File> fileSet = regApplication.getDocumentFiles();
            fileSet.add(file);
            regApplicationService.save(regApplication);

            responseMap.put("name", file.getName());
            responseMap.put("link", ExpertiseUrls.ForwardingFileDownload + "?file_id=" + file.getId());
            responseMap.put("fileId", file.getId());
            responseMap.put("status", 1);
        }
        return responseMap;
    }

    //fileDownload
    @RequestMapping(ExpertiseUrls.ForwardingFileDownload)
    public ResponseEntity<Resource> downloadFile(
            @RequestParam(name = "file_id") Integer fileId
    ){
        User user = userService.getCurrentUserFromContext();

        File file = fileService.findByIdAndUploadUserId(fileId, user.getId());

        if (file == null) {
            return null;
        } else {
            return fileService.getFileAsResourceForDownloading(file);
        }
    }

    //fileDelete
    @RequestMapping(value = ExpertiseUrls.ForwardingFileDelete, method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public HashMap<String, Object> deleteFile(
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "fileId") Integer fileId
    ) {
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id);

        HashMap<String, Object> responseMap = new HashMap<>();
        responseMap.put("status", 0);

        if (regApplication == null) {
            responseMap.put("message", "Object not found.");
            return responseMap;
        }
        File file = fileService.findByIdAndUploadUserId(fileId, user.getId());

        if (file != null) {
            Set<File> fileSet = regApplication.getDocumentFiles();
            if(fileSet.contains(file)) {
                fileSet.remove(file);
                regApplicationService.save(regApplication);

                file.setDeleted(true);
                file.setDateDeleted(new Date());
                file.setDeletedById(user.getId());
                fileService.save(file);

                responseMap.put("status", 1);
            }
        }
        return responseMap;
    }

    private Date setExecutionDate(Date date,Integer deadline){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, deadline);
        Date dateOfExecution = calendar.getTime();
        return dateOfExecution;
    }

}
