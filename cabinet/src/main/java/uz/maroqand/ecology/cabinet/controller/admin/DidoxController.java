package uz.maroqand.ecology.cabinet.controller.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import uz.maroqand.ecology.cabinet.constant.mgmt.MgmtTemplates;
import uz.maroqand.ecology.cabinet.constant.mgmt.MgmtUrls;
import uz.maroqand.ecology.cabinet.controller.expertise.ConclusionCompleteController;
import uz.maroqand.ecology.core.constant.expertise.ApplicantType;
import uz.maroqand.ecology.core.constant.expertise.DidoxStatus;
import uz.maroqand.ecology.core.constant.order.DocumentOrderStatus;
import uz.maroqand.ecology.core.dto.didox.*;
import uz.maroqand.ecology.core.entity.billing.MinWage;
import uz.maroqand.ecology.core.entity.expertise.Conclusion;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.billing.InvoiceService;
import uz.maroqand.ecology.core.service.billing.MinWageService;
import uz.maroqand.ecology.core.service.client.ClientService;
import uz.maroqand.ecology.core.service.expertise.ConclusionService;
import uz.maroqand.ecology.core.service.expertise.RegApplicationService;
import uz.maroqand.ecology.core.service.sys.DidoxService;
import uz.maroqand.ecology.core.service.sys.FileService;
import uz.maroqand.ecology.core.service.sys.SendingDataService;
import uz.maroqand.ecology.core.service.sys.impl.HelperService;
import uz.maroqand.ecology.core.entity.sys.Didox;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;

import java.util.*;

@Controller
public class DidoxController {
    private final RegApplicationService regApplicationService;
    private final HelperService helperService;
    private final DidoxService didoxService;

    private final SendingDataService sendingDataService;
    private final FileService fileService;
    private final InvoiceService invoiceService;
    private final ConclusionService conclusionService;
    private final ClientService clientService;
    private final UserService userService;
    private final MinWageService minWageService;
    private static final Logger logger = LogManager.getLogger(ConclusionCompleteController.class);


    public DidoxController(RegApplicationService regApplicationService, HelperService helperService, DidoxService didoxService,
                           SendingDataService sendingDataService, FileService fileService, InvoiceService invoiceService,
                           ConclusionService conclusionService, ClientService clientService,
                           UserService userService, MinWageService minWageService) {
        this.regApplicationService = regApplicationService;
        this.helperService = helperService;
        this.didoxService = didoxService;
        this.sendingDataService = sendingDataService;
        this.fileService = fileService;
        this.invoiceService = invoiceService;
        this.conclusionService = conclusionService;
        this.clientService = clientService;
        this.userService = userService;
        this.minWageService = minWageService;
    }

    @GetMapping(value = MgmtUrls.DidoxList)
    public String getList(Model model) {
        model.addAttribute("statuses", DidoxStatus.values());
        return MgmtTemplates.DidoxList;
    }


    @GetMapping(value = MgmtUrls.DidoxAjaxList)
    @ResponseBody
    public Map<String, Object> getDidoxAjaxList(
            @RequestParam(name = "didoxId", defaultValue = "", required = false) Integer regAplicationId,
            @RequestParam(name = "didoxStatus", defaultValue = "", required = false) Boolean didoxStatus,
            Pageable pageable) throws JsonProcessingException {

        System.out.println(regAplicationId);
        System.out.println(didoxStatus);
        if (regAplicationId != null) {
            Calendar calendar = Calendar.getInstance();
            Date todayDate = calendar.getTime();
            User user = userService.getCurrentUserFromContext();
            RegApplication regApplication = regApplicationService.getById(regAplicationId);
            Conclusion conclusion = conclusionService.getByRegApplicationIdLast(regApplication.getId());
            if (regApplication.getDidoxId() == null
                    && conclusion != null
                    && conclusion.getDate().compareTo(todayDate) > 0) {
                MinWage minWage = minWageService.getMinWage();
                Product product = new Product();
                product.setOrdno(1);
                product.setCommittentvatregcode("");
                product.setName("Ekologik ekspertiza xulosasi");
                product.setCatalogcode("11603001001000000");
                product.setCatalogname("Услуги по проведению экологической экспертизы");
                product.setBarcode("");
                product.setPackagecode("1506727");
                product.setPackagename("dona");
                product.setCount("1");
                if (!regApplication.getBudget() && (regApplication.getRequirementId() == 5
                        || regApplication.getRequirementId() == 6 || regApplication.getRequirementId() == 7 || regApplication.getRequirementId() == 8)) {
                    product.setSumma(String.format(Locale.US, "%.2f", invoiceService.getInvoice(regApplication.getInvoiceId()).getAmount() / 1.12) + "");
                    product.setVatsum(String.format(Locale.US, "%.2f", invoiceService.getInvoice(regApplication.getInvoiceId()).getAmount() / 1.12 * 0.12) + "");
                    product.setDeliverysumwithvat(String.format(Locale.US, "%.2f", invoiceService.getInvoice(regApplication.getInvoiceId()).getAmount()) + "");
                    product.setDeliverysum(String.format(Locale.US, "%.2f", invoiceService.getInvoice(regApplication.getInvoiceId()).getAmount() / 1.12) + "");
                    product.setVatrate("12");

                } else {
                    product.setSumma(String.format(Locale.US, "%.2f", invoiceService.getInvoice(regApplication.getInvoiceId()).getQty() * minWage.getAmount() / 1.12) + "");
                    product.setVatrate("12");
                    product.setVatsum(String.format(Locale.US, "%.2f", invoiceService.getInvoice(regApplication.getInvoiceId()).getQty() * minWage.getAmount() / 1.12 * 0.12) + "");
                    product.setDeliverysumwithvat(String.format(Locale.US, "%.2f", invoiceService.getInvoice(regApplication.getInvoiceId()).getQty() * minWage.getAmount()) + "");
                    product.setDeliverysum(String.format(Locale.US, "%.2f", invoiceService.getInvoice(regApplication.getInvoiceId()).getQty() * minWage.getAmount() / 1.12) + "");


                }
                product.setWithoutvat(false);
                product.setWithoutexcise(true);
                product.setLgotavatsum(0);
                product.setLgotatype(null);
                product.setLgotaid(null);
                Oldfacturadoc oldfacturadoc = new Oldfacturadoc();
                oldfacturadoc.setOldfacturadate("");
                oldfacturadoc.setOldfacturano("");

                Facturaempowermentdoc facturaempowermentdoc = new Facturaempowermentdoc();
                facturaempowermentdoc.setAgentfacturaid("63987218c659a06a7b433b54");
                facturaempowermentdoc.setEmpowermentdateofissue("");
                facturaempowermentdoc.setAgentfio("");
                facturaempowermentdoc.setAgenttin("");
                facturaempowermentdoc.setEmpowermentno("");
                Facturadoc facturadoc = new Facturadoc();

                if (conclusion != null) {
                    facturadoc.setFacturano(conclusion.getNumber());
                    facturadoc.setFacturadate(Common.uzbekistanDateFormatDidox.format(conclusion.getDate()));
                }
                Contractdoc contractdoc = new Contractdoc();
                contractdoc.setContractdate(Common.uzbekistanDateFormatDidox.format(invoiceService.getInvoice(regApplication.getInvoiceId()).getCreatedDate()));
                contractdoc.setContractno(invoiceService.getInvoice(regApplication.getInvoiceId()).getInvoice());
                //ContractDoc end
                //Seller

                Seller seller = new Seller();
                seller.setName(regApplication.getReview().getName());
                seller.setVatregcode("");
                seller.setAccount(regApplication.getReview().getAccount());
                seller.setBankid("00440");
                seller.setAddress(regApplication.getReview().getAddress());
                seller.setDirector(regApplication.getReview().getDirector());
                seller.setAccountant("");
                seller.setTaxgap("");

                //Seller end
                //Buyer
                Buyer buyer = new Buyer();
                buyer.setName(regApplication.getName());
                buyer.setBranchcode("");
                buyer.setBranchname("");
                buyer.setVatregcode("");
                buyer.setAccount(regApplication.getApplicant().getBankAccount() != null ? regApplication.getApplicant().getBankAccount() : "");
                buyer.setBankid(regApplication.getApplicant().getMfo() != null ? regApplication.getApplicant().getMfo() : "");
                buyer.setAddress(regApplication.getApplicant().getAddress() != null ? regApplication.getApplicant().getAddress() : "");
//        buyer.setVatregstatus(20);
                buyer.setTaxgap("");
                //Buyer end
                Foreigncompany foreigncompany = new Foreigncompany();
                foreigncompany.setCountryid("");
                foreigncompany.setName("");
                foreigncompany.setAddress("");
                foreigncompany.setBank("");
                foreigncompany.setAccount("");
                Expansion expansion = new Expansion();
                expansion.setOrdernumber("");
                Productlist productList = new Productlist();
                LinkedList<Product> products = new LinkedList();
                products.add(product);
                productList.setProducts(products);
                productList.setTin("200934834");
                productList.setHascommittent(false);
                productList.setHaslgota(false);
                productList.setHasexcise(false);
                productList.setHasvat(true);
                productList.setFacturaproductid("");
                DocumentJson json = new DocumentJson();
                json.setVersion(1);
                json.setDidoxcontractid("");
                json.setFacturatype(0);
                json.setFacturaid("1");
                json.setProductlist(productList);
                json.setBuyer(buyer);
                json.setSeller(seller);
                json.setContractdoc(contractdoc);
                json.setFacturadoc(facturadoc);
                json.setContractdoc(contractdoc);
                json.setWaybillids(null);
                json.setExpansion(expansion);
                json.setFacturatype(0);
                json.setOldfacturadoc(oldfacturadoc);
                json.setHasmarking(false);
                if (regApplication.getApplicantId() != null && clientService.getById(regApplication.getApplicantId()).getType() == ApplicantType.Individual) {
                    json.setBuyertin(regApplication.getApplicantId() != null ? clientService.getById(regApplication.getApplicantId()).getPinfl() : "");
                } else {
                    json.setBuyertin(regApplication.getApplicantId() != null ? clientService.getById(regApplication.getApplicantId()).getTin().toString() : "");
                }
                json.setSellertin("200934834");
                Itemreleaseddoc itemreleaseddoc = new Itemreleaseddoc();
                itemreleaseddoc.setItemreleasedfio("");
                json.setItemreleaseddoc(itemreleaseddoc);
                json.setFacturaid("");
                json.setFacturaempowermentdoc(facturaempowermentdoc);

                HttpHeaders headersDidox = new HttpHeaders();
                headersDidox.setContentType(MediaType.APPLICATION_JSON);
                headersDidox.add("user-key", regApplicationService.getUserKey());
                logger.info("userkey:{}", regApplicationService.getUserKey());
                HttpEntity<DocumentJson> requestEntityDidox =
                        new HttpEntity<>(json, headersDidox);
                logger.info("requestEntityDidox:{}", requestEntityDidox);

                ObjectMapper mapper = new ObjectMapper();
                String jsonInString = mapper.writeValueAsString(json);

                logger.info("jsonInString {}", jsonInString);

                RestTemplate restTemplateDidox = new RestTemplate();
                HttpEntity<Object> request = new HttpEntity<>(json, headersDidox);
                ResponseEntity<Root> responseDidox = null;
                ResponseEntity<Object> responseDidoxConfirm = null;

                String jsonInStringRequest = mapper.writeValueAsString(request);
                Didox didox = new Didox();
                didox.setRegApplicationId(regApplication.getId());
                didox.setCreatedById(user.getId());
                didox.setCreatedAt(new Date());
                didox.setStatus(DocumentOrderStatus.Pending);
                didox.setDeleted(false);
                didox.setParams(jsonInStringRequest);
                logger.info("jsonInStringRequest {}", jsonInStringRequest);
                try {
//            response = restTemplate.exchange("https://register.soliq.uz/reestr-api/create", HttpMethod.POST, request, Object.class);
                    responseDidox = restTemplateDidox.exchange("https://api.didox.uz/v1/documents/002/create", HttpMethod.POST, request, Root.class);
                    logger.info("responseDidox:{}", responseDidox);
                    logger.info("responseDidoxId:{}", responseDidox.getBody().get_id());
                    regApplication.setDidoxStatus(DidoxStatus.Pending);
                    if (responseDidox.getBody() != null) {
                        regApplication.setDidoxId(responseDidox.getBody().get_id());
                        regApplicationService.update(regApplication);
                    }
                    didox.setResponse(mapper.writeValueAsString(responseDidox.getBody()));
                } catch (Exception e) {
                    didox.setResponse(e.getMessage());
                    regApplication.setDidoxStatus(DidoxStatus.Error);
                    regApplicationService.update(regApplication);
                    logger.info("Exception:{}", e.getMessage());
                }
                didoxService.save(didox);

            }

        }
        if (didoxStatus != null && didoxStatus) {

            List<RegApplication> regApplicationList = regApplicationService.getByDidoxStatusAndDeletedFalse(DidoxStatus.getDidoxType(1));
            for (RegApplication regApplication : regApplicationList) {


//        if (regAplicationId != null) {
                Calendar calendar = Calendar.getInstance();
                Date todayDate = calendar.getTime();
                User user = userService.getCurrentUserFromContext();
                Conclusion conclusion = conclusionService.getByRegApplicationIdLast(regApplication.getId());
                if (regApplication.getDidoxId() == null && conclusion != null && conclusion.getDate().compareTo(todayDate) > 0) {
                    MinWage minWage = minWageService.getMinWage();
                    Product product = new Product();
                    product.setOrdno(1);
                    product.setCommittentvatregcode("");
                    product.setName("Ekologik ekspertiza xulosasi");
                    product.setCatalogcode("11603001001000000");
                    product.setCatalogname("Услуги по проведению экологической экспертизы");
                    product.setBarcode("");
                    product.setPackagecode("1506727");
                    product.setPackagename("dona");
                    product.setCount("1");
                    if (!regApplication.getBudget() && (regApplication.getRequirementId() == 5
                            || regApplication.getRequirementId() == 6 || regApplication.getRequirementId() == 7 || regApplication.getRequirementId() == 8)) {
                        product.setSumma(String.format(Locale.US, "%.2f", invoiceService.getInvoice(regApplication.getInvoiceId()).getAmount() / 1.12) + "");
                        product.setVatsum(String.format(Locale.US, "%.2f", invoiceService.getInvoice(regApplication.getInvoiceId()).getAmount() / 1.12 * 0.12) + "");
                        product.setDeliverysumwithvat(String.format(Locale.US, "%.2f", invoiceService.getInvoice(regApplication.getInvoiceId()).getAmount()) + "");
                        product.setDeliverysum(String.format(Locale.US, "%.2f", invoiceService.getInvoice(regApplication.getInvoiceId()).getAmount() / 1.12) + "");
                        product.setVatrate("12");

                    } else {
                        product.setSumma(String.format(Locale.US, "%.2f", invoiceService.getInvoice(regApplication.getInvoiceId()).getQty() * minWage.getAmount() / 1.12) + "");
                        product.setVatrate("12");
                        product.setVatsum(String.format(Locale.US, "%.2f", invoiceService.getInvoice(regApplication.getInvoiceId()).getQty() * minWage.getAmount() / 1.12 * 0.12) + "");
                        product.setDeliverysumwithvat(String.format(Locale.US, "%.2f", invoiceService.getInvoice(regApplication.getInvoiceId()).getQty() * minWage.getAmount()) + "");
                        product.setDeliverysum(String.format(Locale.US, "%.2f", invoiceService.getInvoice(regApplication.getInvoiceId()).getQty() * minWage.getAmount() / 1.12) + "");


                    }
                    product.setWithoutvat(false);
                    product.setWithoutexcise(true);
                    product.setLgotavatsum(0);
                    product.setLgotatype(null);
                    product.setLgotaid(null);
                    Oldfacturadoc oldfacturadoc = new Oldfacturadoc();
                    oldfacturadoc.setOldfacturadate("");
                    oldfacturadoc.setOldfacturano("");

                    Facturaempowermentdoc facturaempowermentdoc = new Facturaempowermentdoc();
                    facturaempowermentdoc.setAgentfacturaid("63987218c659a06a7b433b54");
                    facturaempowermentdoc.setEmpowermentdateofissue("");
                    facturaempowermentdoc.setAgentfio("");
                    facturaempowermentdoc.setAgenttin("");
                    facturaempowermentdoc.setEmpowermentno("");
                    Facturadoc facturadoc = new Facturadoc();

                    if (conclusion != null) {
                        facturadoc.setFacturano(conclusion.getNumber());
                        facturadoc.setFacturadate(Common.uzbekistanDateFormatDidox.format(conclusion.getDate()));
                    }
                    Contractdoc contractdoc = new Contractdoc();
                    contractdoc.setContractdate(Common.uzbekistanDateFormatDidox.format(invoiceService.getInvoice(regApplication.getInvoiceId()).getCreatedDate()));
                    contractdoc.setContractno(invoiceService.getInvoice(regApplication.getInvoiceId()).getInvoice());
                    //ContractDoc end
                    //Seller

                    Seller seller = new Seller();
                    seller.setName(regApplication.getReview().getName());
                    seller.setVatregcode("");
                    seller.setAccount(regApplication.getReview().getAccount());
                    seller.setBankid("00440");
                    seller.setAddress(regApplication.getReview().getAddress());
                    seller.setDirector(regApplication.getReview().getDirector());
                    seller.setAccountant("");
                    seller.setTaxgap("");

                    //Seller end
                    //Buyer
                    Buyer buyer = new Buyer();
                    buyer.setName(regApplication.getName());
                    buyer.setBranchcode("");
                    buyer.setBranchname("");
                    buyer.setVatregcode("");
                    buyer.setAccount(regApplication.getApplicant().getBankAccount() != null ? regApplication.getApplicant().getBankAccount() : "");
                    buyer.setBankid(regApplication.getApplicant().getMfo() != null ? regApplication.getApplicant().getMfo() : "");
                    buyer.setAddress(regApplication.getApplicant().getAddress() != null ? regApplication.getApplicant().getAddress() : "");
//        buyer.setVatregstatus(20);
                    buyer.setTaxgap("");
                    //Buyer end
                    Foreigncompany foreigncompany = new Foreigncompany();
                    foreigncompany.setCountryid("");
                    foreigncompany.setName("");
                    foreigncompany.setAddress("");
                    foreigncompany.setBank("");
                    foreigncompany.setAccount("");
                    Expansion expansion = new Expansion();
                    expansion.setOrdernumber("");
                    Productlist productList = new Productlist();
                    LinkedList<Product> products = new LinkedList();
                    products.add(product);
                    productList.setProducts(products);
                    productList.setTin("200934834");
                    productList.setHascommittent(false);
                    productList.setHaslgota(false);
                    productList.setHasexcise(false);
                    productList.setHasvat(true);
                    productList.setFacturaproductid("");
                    DocumentJson json = new DocumentJson();
                    json.setVersion(1);
                    json.setDidoxcontractid("");
                    json.setFacturatype(0);
                    json.setFacturaid("1");
                    json.setProductlist(productList);
                    json.setBuyer(buyer);
                    json.setSeller(seller);
                    json.setContractdoc(contractdoc);
                    json.setFacturadoc(facturadoc);
                    json.setContractdoc(contractdoc);
                    json.setWaybillids(null);
                    json.setExpansion(expansion);
                    json.setFacturatype(0);
                    json.setOldfacturadoc(oldfacturadoc);
                    json.setHasmarking(false);
                    if (regApplication.getApplicantId() != null && clientService.getById(regApplication.getApplicantId()).getType() == ApplicantType.Individual) {
                        json.setBuyertin(regApplication.getApplicantId() != null ? clientService.getById(regApplication.getApplicantId()).getPinfl() : "");
                    } else {
                        json.setBuyertin(regApplication.getApplicantId() != null ? clientService.getById(regApplication.getApplicantId()).getTin().toString() : "");
                    }
                    json.setSellertin("200934834");
                    Itemreleaseddoc itemreleaseddoc = new Itemreleaseddoc();
                    itemreleaseddoc.setItemreleasedfio("");
                    json.setItemreleaseddoc(itemreleaseddoc);
                    json.setFacturaid("");
                    json.setFacturaempowermentdoc(facturaempowermentdoc);

                    HttpHeaders headersDidox = new HttpHeaders();
                    headersDidox.setContentType(MediaType.APPLICATION_JSON);
                    headersDidox.add("user-key", regApplicationService.getUserKey());
                    logger.info("userkey:{}", regApplicationService.getUserKey());
                    HttpEntity<DocumentJson> requestEntityDidox =
                            new HttpEntity<>(json, headersDidox);
                    logger.info("requestEntityDidox:{}", requestEntityDidox);

                    ObjectMapper mapper = new ObjectMapper();
                    String jsonInString = mapper.writeValueAsString(json);

                    logger.info("jsonInString {}", jsonInString);

                    RestTemplate restTemplateDidox = new RestTemplate();
                    HttpEntity<Object> request = new HttpEntity<>(json, headersDidox);
                    ResponseEntity<Root> responseDidox = null;
                    ResponseEntity<Object> responseDidoxConfirm = null;

                    String jsonInStringRequest = mapper.writeValueAsString(request);
                    Didox didox = new Didox();
                    didox.setRegApplicationId(regApplication.getId());
                    didox.setCreatedById(user.getId());
                    didox.setCreatedAt(new Date());
                    didox.setStatus(DocumentOrderStatus.Pending);
                    didox.setDeleted(false);
                    didox.setParams(jsonInStringRequest);
                    logger.info("jsonInStringRequest {}", jsonInStringRequest);
                    try {
//            response = restTemplate.exchange("https://register.soliq.uz/reestr-api/create", HttpMethod.POST, request, Object.class);
                        responseDidox = restTemplateDidox.exchange("https://api.didox.uz/v1/documents/002/create", HttpMethod.POST, request, Root.class);
                        logger.info("responseDidox:{}", responseDidox);
                        logger.info("responseDidoxId:{}", responseDidox.getBody().get_id());
                        regApplication.setDidoxStatus(DidoxStatus.Pending);
                        if (responseDidox.getBody() != null) {
                            regApplication.setDidoxId(responseDidox.getBody().get_id());
                            regApplicationService.update(regApplication);
                        }
                        didox.setResponse(mapper.writeValueAsString(responseDidox.getBody()));
                    } catch (Exception e) {
                        didox.setResponse(e.getMessage());
                        regApplication.setDidoxStatus(DidoxStatus.Error);
                        regApplicationService.update(regApplication);
                        logger.info("Exception:{}", e.getMessage());
                    }
                    didoxService.save(didox);

                }

                System.out.println(regAplicationId);


            }
            System.out.println(didoxStatus);


        }
        Page<Didox> list = didoxService.getAjaxList(pageable);
        Map<String, Object> map = new HashMap<>();
        String local = LocaleContextHolder.getLocale().toLanguageTag();
        map.put("recordsTotal", list.getTotalElements());
        map.put("recordsFiltered", list.getTotalElements());
        List<Object[]> convenientForJSONArray = new ArrayList<>(list.getContent().size());
        for (Didox didox : list.getContent()) {
            convenientForJSONArray.add(new Object[]{
                    didox.getId(),
                    didox.getParams(),
                    didox.getStatus(),
                    didox.getResponse(),
                    didox.getDeleted(),
                    didox.getRegApplicationId() != null ? didox.getRegApplicationId() : " ",
                    didox.getCreatedAt() != null ? Common.uzbekistanDateFormat.format(didox.getCreatedAt()) : " ",
                    didox.getUpdateAt(),
                    didox.getCreatedById(),
                    didox.getUpdateById(),
                    didox.getRegApplicationId() != null ? regApplicationService.getById(didox.getRegApplicationId()).getMaterials()
                            != null ? helperService.getMaterialShortNames(regApplicationService.getById(didox.getRegApplicationId()).getMaterials(), local) : "" : ""

            });
        }
        map.put("data", convenientForJSONArray);
        return map;

    }
}



