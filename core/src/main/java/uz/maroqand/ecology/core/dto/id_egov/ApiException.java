package uz.maroqand.ecology.core.dto.id_egov;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ApiException extends RuntimeException {
    private ResponseStatus responseStatus;
}
