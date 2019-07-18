package uz.maroqand.ecology.core.service.sys;

import uz.maroqand.ecology.core.constant.sys.TableHistoryEntity;
import uz.maroqand.ecology.core.constant.sys.TableHistoryType;
import uz.maroqand.ecology.core.entity.sys.TableHistory;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Utkirbek Boltaev on 20.05.2019.
 * (uz)
 */
public interface TableHistoryService {

    TableHistory create(
            TableHistoryType type,
            TableHistoryEntity entity,
            Integer entityId,
            String before,
            String after,
            String message,
            Integer userId,
            Integer userAdditionalId
    );

    List<TableHistory> getByEntityId(TableHistoryEntity entity, Integer entityId);

    List<HashMap<String,Object>> forAudit(Type type,TableHistoryEntity tableHistoryEntity,Integer id);

}
