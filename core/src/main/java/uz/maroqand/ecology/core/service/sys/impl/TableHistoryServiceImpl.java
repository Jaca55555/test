package uz.maroqand.ecology.core.service.sys.impl;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.constant.sys.TableHistoryEntity;
import uz.maroqand.ecology.core.constant.sys.TableHistoryType;
import uz.maroqand.ecology.core.entity.sys.TableHistory;
import uz.maroqand.ecology.core.repository.sys.TableHistoryRepository;
import uz.maroqand.ecology.core.service.sys.TableHistoryService;
import uz.maroqand.ecology.core.service.user.UserAdditionalService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Utkirbek Boltaev on 20.05.2019.
 * (uz)
 */
@Service
public class TableHistoryServiceImpl implements TableHistoryService {

    private final TableHistoryRepository tableHistoryRepository;
    private final Gson gson;
    private final UserService userService;
    private final UserAdditionalService userAdditionalService;
    @Autowired
    public TableHistoryServiceImpl(TableHistoryRepository tableHistoryRepository, Gson gson, UserService userService, UserAdditionalService userAdditionalService) {
        this.tableHistoryRepository = tableHistoryRepository;
        this.gson = gson;
        this.userService = userService;
        this.userAdditionalService = userAdditionalService;
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

    @Override
    public List<HashMap<String, Object>> forAudit(Type type, TableHistoryEntity tableHistoryEntity, Integer id) {

        List<TableHistory> tableHistoryList = getByEntityId(tableHistoryEntity,id);
        List<HashMap<String,Object>> beforeAndAfterList = new ArrayList<>();
        for (TableHistory tableHistory: tableHistoryList){
            HashMap<String,Object> stringCategoryHashMap = new HashMap<>();
            List<Object> objectList = gson.fromJson("["+tableHistory.getChangesSerialized()+"]",type);
            stringCategoryHashMap.put("before",objectList.get(0));
            stringCategoryHashMap.put("after",objectList.get(1));
            stringCategoryHashMap.put("userName",userService.findById(tableHistory.getUserId()).getUsername());
            stringCategoryHashMap.put("registeredDate", tableHistory.getRegisteredDate()!=null? Common.uzbekistanDateFormat.format(tableHistory.getRegisteredDate()) : "");
            stringCategoryHashMap.put("userAdditional", tableHistory.getUserAdditionalId()!=null?userAdditionalService.getById(tableHistory.getUserAdditionalId()):null);
            beforeAndAfterList.add(stringCategoryHashMap);
        }
        return beforeAndAfterList;
    }

}