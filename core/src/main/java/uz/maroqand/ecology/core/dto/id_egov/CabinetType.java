package uz.maroqand.ecology.core.dto.id_egov;

import lombok.Getter;

@Getter
public enum CabinetType {
    BACK_OFFICE("ekoekspertiza_uz", "ekoekspertiza_uz", "Jp0h5U0fZ3xdy9ifJG4DansF");

    //UzJqmfISjtaXD9rW2O7sT4gB

    private final String client_id;
    private final String scope;
    private final String secret;

    CabinetType(String client_id, String scope, String secret) {
        this.client_id = client_id;
        this.scope = scope;
        this.secret = secret;
    }

}