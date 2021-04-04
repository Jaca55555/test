package uz.maroqand.ecology.core.dto.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@AllArgsConstructor
public class ResponseDTO {
    private Integer statusCode;
    private String statusMessage;
    private Object data;

    public ResponseDTO(Object data) {
        this.statusCode = 0;
        this.statusMessage = "OK";
        this.data = data;
    }
}
