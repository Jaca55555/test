package uz.maroqand.ecology.cabinet.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import uz.maroqand.ecology.cabinet.constant.expertise.ExpertiseTemplates;
import uz.maroqand.ecology.cabinet.constant.expertise.ExpertiseUrls;
import uz.maroqand.ecology.core.config.DatabaseMessageSource;
import uz.maroqand.ecology.core.constant.order.DocumentOrderType;
import uz.maroqand.ecology.core.entity.DocumentOrder;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.repository.DocumentOrdersRepository;
import uz.maroqand.ecology.core.service.DocumentOrdersService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Utkirbek Boltaev on 24.07.2018.
 * (uz)
 * (ru)
 */
@Controller
public class OrderedDocumentsController {

    @Autowired
    private DocumentOrdersRepository documentOrdersRepository;

    @Autowired
    private DocumentOrdersService documentOrdersService;

    @Autowired
    private UserService usersService;


    private static DatabaseMessageSource databaseMessageSource;

    public static void setTranslationsSource(DatabaseMessageSource translationsMessageSource) {
        databaseMessageSource = translationsMessageSource;
    }

    @RequestMapping(ExpertiseUrls.OrderedDocsList)
    public String getOrderedDocsListPage(Model model) {
        model.addAttribute("ajax_link", ExpertiseUrls.OrderedDocsListAjax);
        model.addAttribute("download_link", ExpertiseUrls.OrderedDocsDownload);
        model.addAttribute("appealTypeList", DocumentOrderType.values());
        return ExpertiseTemplates.OrderedDocsList;
    }

    @RequestMapping(value = ExpertiseUrls.OrderedDocsListAjax, produces = "application/json")
    @ResponseBody
    public HashMap<String, Object> getOrderedDocsListAsAjax(
            Pageable pageable
    ) {
        User user = usersService.getCurrentUserFromContext();

        Page<DocumentOrder> ordersPage = documentOrdersRepository.findByDeletedFalseAndOrderedByIdOrderByIdDesc(user.getId(), pageable);

        HashMap<String, Object> result = new HashMap<>();
        result.put("recordsTotal", ordersPage.getTotalElements()); //Total elements
        result.put("recordsFiltered", ordersPage.getTotalElements()); //Filtered elements
        List<DocumentOrder> ordersList = ordersPage.getContent();
        List<Object[]> convenientForJSONArray = new ArrayList<>(ordersList.size());
        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        for (DocumentOrder documentOrder : ordersList) {
            System.out.println();
            convenientForJSONArray.add(new Object[]{
                    documentOrder.getId(),
                    documentOrder.getType()!=null ?documentOrder.getType().name():"",
                    documentOrder.getRegisteredAt() != null ? Common.uzbekistanDateAndTimeFormat.format(documentOrder.getRegisteredAt()) : "",
                    documentOrder.getStatus().ordinal(),
                    documentOrdersService.isDownloadable(documentOrder)
            });
        }

        result.put("data", convenientForJSONArray);
        return result;
    }


    @RequestMapping(value = ExpertiseUrls.OrderedDocsDownload)
    @ResponseBody
    public ResponseEntity<Resource> downloadOrderedDocumentFile(
            HttpServletResponse response,
            @RequestParam("id") Integer documentOrderId
    ) {
        User user = usersService.getCurrentUserFromContext();

        DocumentOrder documentOrder = documentOrdersRepository.findByIdAndDeletedFalseAndOrderedById(documentOrderId, user.getId());
        if (documentOrdersService.isDownloadable(documentOrder)) {
            return documentOrdersService.documentOrderToDownloadableResource(documentOrder);
        } else {
            return null;
        }
    }

    /*
        M O N I T O R I N G
    */

    @RequestMapping(ExpertiseUrls.MonitoringOrderedDocs)
    public String getOrderedDocs(
            Model model
    ) {
        User user = usersService.getCurrentUserFromContext();

        model.addAttribute("ajax_link", ExpertiseUrls.MonitoringOrderedDocsListAjax);
        model.addAttribute("download_link", ExpertiseUrls.OrderedDocsDownload);
        return ExpertiseTemplates.MntrOrderedDocsList;

    }

    @RequestMapping(ExpertiseUrls.MonitoringOrderedDocsListAjax)
    @ResponseBody
    public HashMap<String, Object> getMonitoringOrderedDocsListAsAjax(
            Pageable pageable
    ) {

        Page<DocumentOrder> ordersPage = documentOrdersRepository.findByDeletedFalseOrderByIdDesc(pageable);

        HashMap<String, Object> result = new HashMap<>();
        result.put("recordsTotal", ordersPage.getTotalElements()); //Total elements
        result.put("recordsFiltered", ordersPage.getTotalElements()); //Filtered elements

        List<DocumentOrder> ordersList = ordersPage.getContent();

        List<Object[]> convenientForJSONArray = new ArrayList<>(ordersList.size());

        String locale = LocaleContextHolder.getLocale().toLanguageTag();

        for (DocumentOrder documentOrder : ordersList) {
            String authorStr = "";
            if (documentOrder.getOrderedBy() != null) {
                User author = documentOrder.getOrderedBy();
                authorStr = author.getId() + "." + author.getLastname() + " " + author.getFirstname();
                /*if (author.getUserCategory() != null) {
                    authorStr += "<br/>" + author.getUserCategory().getName();
                }*/
            }
            convenientForJSONArray.add(new Object[]{
                    documentOrder.getId(),
                    databaseMessageSource.resolveCodeSimply("sys_documentOrders.type" + documentOrder.getType().name(), locale),
                    documentOrder.getRegisteredAt() != null ? Common.uzbekistanDateAndTimeFormat.format(documentOrder.getRegisteredAt()) : "",
                    documentOrder.getStartedAt() != null ? Common.uzbekistanDateAndTimeFormat.format(documentOrder.getStartedAt()) : "",
                    documentOrder.getFinishedAt() != null ? Common.uzbekistanDateAndTimeFormat.format(documentOrder.getFinishedAt()) : "",
                    authorStr,
                    documentOrder.getStatus().ordinal(),
                    documentOrdersService.isDownloadable(documentOrder)
            });
        }

        result.put("data", convenientForJSONArray);
        return result;
    }


}
