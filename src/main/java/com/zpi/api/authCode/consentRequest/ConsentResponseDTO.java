package com.zpi.api.authCode.consentRequest;

import com.zpi.domain.authCode.consentRequest.ConsentResponse;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class ConsentResponseDTO {
    private final String code;
    private final String state;

    public ConsentResponseDTO(ConsentResponse response) {
        this.code = response.getCode().getValue();
        this.state = response.getState();
    }
}
