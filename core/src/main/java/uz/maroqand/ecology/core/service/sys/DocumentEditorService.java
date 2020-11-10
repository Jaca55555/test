package uz.maroqand.ecology.core.service.sys;

import uz.maroqand.ecology.core.entity.expertise.Conclusion;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;
import uz.maroqand.ecology.core.entity.sys.DocumentRepo;

import java.util.List;

/**
 * Created by Utkirbek Boltaev on 03.06.2019.
 * (uz)
 * (ru)
 */
public interface DocumentEditorService {

    List<String[]> getDataForReplacingInMurojaatBlanki(RegApplication regApplication, String locale);

    boolean buildMurojaatBlanki(RegApplication regApplication, List<String[]> forReplacing, Integer userId);

    Boolean conclusionComplete(Conclusion conclusion);


}
