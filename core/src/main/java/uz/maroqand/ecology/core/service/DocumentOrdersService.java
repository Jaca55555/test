package uz.maroqand.ecology.core.service;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.constant.order.DocumentOrderParams;
import uz.maroqand.ecology.core.constant.order.DocumentOrderStatus;
import uz.maroqand.ecology.core.constant.order.DocumentOrderType;
import uz.maroqand.ecology.core.entity.DocumentOrder;
import uz.maroqand.ecology.core.entity.sys.File;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.repository.DocumentOrdersRepository;
import uz.maroqand.ecology.core.service.sys.FileService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.core.util.FileNameParser;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Utkirbek Boltaev on 24.07.2018.
 * (uz)
 * (ru)
 */
@Service
public class DocumentOrdersService {

    private final FileService filesService;

    private final DocumentOrdersRepository documentOrdersRepository;

    private Gson gson = new Gson();

    private Logger logger = LogManager.getLogger(DocumentOrdersService.class);

    private boolean keepWorking = true;

    private LinkedBlockingQueue<Integer> ordersQueue;

    private Thread worker = null;

    private Map<DocumentOrderType, DocumentOrderPerformer> orderTypeToPerformerMap;

    private ConcurrentMap<Integer, Integer> usersToOrdersMap;

    @Autowired
    public DocumentOrdersService(FileService filesService, DocumentOrdersRepository repository) {
        this.filesService = filesService;
        ordersQueue = new LinkedBlockingQueue<>();
        orderTypeToPerformerMap = new HashMap<>();
        usersToOrdersMap = new ConcurrentHashMap<>();
        documentOrdersRepository = repository;
        queueUnfinishedTasks();
    }

    private void queueUnfinishedTasks() {
        List<DocumentOrderStatus> statusesForIgnoring = new ArrayList<>(2);
        statusesForIgnoring.add(DocumentOrderStatus.Pending);
        statusesForIgnoring.add(DocumentOrderStatus.InProgress);
        List<Integer> pendingIds = documentOrdersRepository.findIdsByStatusInAndDeletedFalse(statusesForIgnoring);
        for (Integer id : pendingIds) {
            ordersQueue.add(id);
        }
    }

    public void registerPerformer(DocumentOrderType type, DocumentOrderPerformer performer) {
        orderTypeToPerformerMap.put(type, performer);
    }

    public void startWorkingOnOrders() {
        keepWorking = true;
        workOnOrders();
    }

    public void stopWorkingOnOrders() {
        keepWorking = false;
    }

    public boolean orderDocument(DocumentOrderParams orderedDocumentData,DocumentOrderType type, User author, String locale) {
        if (!usersToOrdersMap.containsKey(author.getId())) usersToOrdersMap.put(author.getId(), 0);
        if (usersToOrdersMap.get(author.getId()) > 0) {
            //Aktiv zakazi bor
            return false;
        }
        usersToOrdersMap.put(author.getId(), usersToOrdersMap.get(author.getId()) + 1);

        DocumentOrder documentOrder = new DocumentOrder();
        documentOrder.setOrderedBy(author);
        documentOrder.setRegisteredAt(new Date());
        documentOrder.setType(type);
        documentOrder.setStatus(DocumentOrderStatus.Pending);
        documentOrder.setParams(gson.toJson(orderedDocumentData));
        documentOrder.setLocale(locale);
        documentOrder.setDeleted(false);
        DocumentOrder savedOrder = documentOrdersRepository.saveAndFlush(documentOrder);
        ordersQueue.add(savedOrder.getId());
        return true;
    }

    private void setFileNameForOrder(DocumentOrder order) {
        order.setFileName(Common.TemporaryExcelFiles + order.getId() + "_" + order.getOrderedBy().getId() + "_" + (new Date()).getTime());
    }

    public boolean isDownloadable(DocumentOrder documentOrder) {
        return (documentOrder != null && documentOrder.getFileName() != null && !documentOrder.getFileName().isEmpty());
    }

    public ResponseEntity<Resource> documentOrderToDownloadableResource(DocumentOrder documentOrder) {
        File file = new File();
        file.setPath(documentOrder.getFileName());
        file.setSize(documentOrder.getSize().intValue());
        file.setExtension(FileNameParser.getExtensionFromFileName(documentOrder.getFileName()));
        file.setName("docOrder_" + documentOrder.getId() + "." + file.getExtension());
        return filesService.getFileAsResourceForDownloading(file);
    }

    public Map<String, Object> getFrontendResponseForOrder(boolean queued, String locale) {
        HashMap<String, Object> result = new HashMap<>();
        if (queued) {
            result.put("status", 1);
            if (locale.equals("uz")) {
                result.put("message", "Hujjat buyurtmangiz qabul qilindi.\n\nBuyurtmangiz tayyor bo`lganda 'Buyurtirilgan hujjatlar' bo`limidan yuklab olishingiz mumkin.");
            } else {
                result.put("message", "Ваш заказ на документ принят.\n\nВы сможете скачать Ваш заказ в разделе 'Заказанные документы'.");
            }
        } else {
            result.put("status", 0);
            if (locale.equals("uz")) {
                result.put("message", "Diqqat! Hujjatga buyurtmangiz qabul qilinmadi.\nChunki siz boshqa hujjatni buyurtma berib bo`lgansiz. Yana hujjat buyurtma berish uchun avvalgi buyurtmangiz tayyor bo`lishini kuting.\n\nBuyurtmangiz tayyor bo`lganda 'Buyurtirilgan hujjatlar' bo`limidan yuklab olishingiz mumkin.");
            } else {
                result.put("message", "Внимание! Ваш заказ на документ не принят.\nПотому что, Вы уже заказывали другой документ. Что бы заказать ещё, дождитесь завершения Вашего предидущего заказа.\n\nВы сможете скачать Ваш заказ в разделе 'Заказанные документы'.");
            }
        }
        return result;
    }


    public boolean deleteDocumentForever(DocumentOrder order) {
        if (order == null || order.getFileName() == null || order.getFileName().isEmpty()) {
            return false;
        }
        boolean result = false;
        try {
            java.io.File file = new java.io.File(order.getFileName());

            if (file.exists()) {
                result = file.delete();
            }
        } catch (Exception e) {
            result = false;
        }
        if (result) {
            order.setFileName("");
            order.setDeleted(true);
            documentOrdersRepository.saveAndFlush(order);
        }
        return result;
    }

    private class Worker implements Runnable {

        @Override
        public void run() {
            while (keepWorking && !Thread.interrupted()) {
                Integer nextOrderId = null;
                try {
                    nextOrderId = ordersQueue.take();
                } catch (Exception e) {
                    continue;
                }

                DocumentOrder documentOrder = documentOrdersRepository.findById(nextOrderId).get();
                if (documentOrder == null) continue;

                if (
                        DocumentOrderStatus.Successfull.equals(documentOrder.getStatus()) ||
                                DocumentOrderStatus.Failed.equals(documentOrder.getStatus())
                        ) {
                    continue;
                }

                if (usersToOrdersMap.containsKey(documentOrder.getOrderedBy().getId())) {
                    usersToOrdersMap.put(documentOrder.getOrderedBy().getId(), usersToOrdersMap.get(documentOrder.getOrderedBy().getId()) - 1);
                }

                documentOrder.setStartedAt(new Date());
                documentOrder.setStatus(DocumentOrderStatus.InProgress);
                documentOrdersRepository.saveAndFlush(documentOrder);

                if (!orderTypeToPerformerMap.containsKey(documentOrder.getType())) {
                    logger.fatal("No order performer found for type: {}", documentOrder.getType());
                    documentOrder.setStatus(DocumentOrderStatus.Failed);
                    documentOrder.setFinishedAt(new Date());
                    documentOrdersRepository.saveAndFlush(documentOrder);
                    continue;
                }
                try {
                    setFileNameForOrder(documentOrder);
                    if (orderTypeToPerformerMap.get(documentOrder.getType()).performDocumentOrder(documentOrder)) {
                        documentOrder.setStatus(DocumentOrderStatus.Successfull);
                        java.io.File file = new java.io.File(documentOrder.getFileName());
                        documentOrder.setSize(file.length());
                    } else {
                        documentOrder.setStatus(DocumentOrderStatus.Failed);
                    }
                }
                catch (Exception e){
                    logger.error("documentOrder#{} Exception thrown: ", e);
                    documentOrder.setStatus(DocumentOrderStatus.Failed);
                }
                logger.info("Finished working on documentOrder#{} {}", documentOrder.getId(), documentOrder.getStatus());
                documentOrder.setFinishedAt(new Date());
                documentOrdersRepository.saveAndFlush(documentOrder);

            }
        }
    }

    private void workOnOrders() {
        if (worker == null) worker = new Thread(new Worker());
        worker.start();
    }
}
