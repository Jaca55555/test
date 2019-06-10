package uz.maroqand.ecology.core.service.sys.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.constant.sys.TableHistoryEntity;
import uz.maroqand.ecology.core.constant.sys.TableHistoryType;
import uz.maroqand.ecology.core.entity.sys.TableHistory;
import uz.maroqand.ecology.core.repository.sys.TableHistoryRepository;
import uz.maroqand.ecology.core.service.sys.TableHistoryService;

import java.util.Date;
import java.util.List;

/**
 * Created by Utkirbek Boltaev on 20.05.2019.
 * (uz)
 */
@Service
public class TableHistoryServiceImpl implements TableHistoryService {

    private final TableHistoryRepository tableHistoryRepository;

    @Autowired
    public TableHistoryServiceImpl(TableHistoryRepository tableHistoryRepository) {
        this.tableHistoryRepository = tableHistoryRepository;
    }

    @Override
    public TableHistory create(
            TableHistoryType type,
            TableHistoryEntity entity,
            Integer entityId,
            String before,
            String after,
            String message,
            Integer userId,
            Integer userAdditionalId
    ){
        TableHistory tableHistory = new TableHistory();
        tableHistory.setType(type);
        tableHistory.setEntity(entity);
        tableHistory.setEntityId(entityId);
        tableHistory.setChangesSerialized(before+","+after);

        tableHistory.setMessage(message);
        tableHistory.setUserId(userId);
        tableHistory.setUserAdditionalId(userAdditionalId);
        tableHistory.setRegisteredDate(new Date());

        return tableHistoryRepository.save(tableHistory);
    }

    @Override
    public List<TableHistory> getByEntityId(TableHistoryEntity entity, Integer entityId){
        return tableHistoryRepository.findByEntityAndEntityIdOrderByIdDesc(entity,entityId);
    }

}