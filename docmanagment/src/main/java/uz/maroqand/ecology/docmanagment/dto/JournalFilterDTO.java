package uz.maroqand.ecology.docmanagment.dto;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by Namazov Jamshid
 * email: <jamwid07@mail.ru>
 * date: 24.12.2019
 */

@Data
public class JournalFilterDTO {
    private Integer docTypeId;
    private String name;
    private String prefix;
    private String suffix;
    private Boolean status;

    public String getName() {
        return StringUtils.trimToNull(name);
    }
    public String getPrefix() {
        return StringUtils.trimToNull(prefix);
    }
    public String getSuffix() {
        return StringUtils.trimToNull(suffix);
    }
}
