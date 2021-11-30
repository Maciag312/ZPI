package com.zpi.api.authCode.consentRequest;

import com.zpi.domain.authCode.consentRequest.ConsentResponse;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.util.UriComponentsBuilder;

@Getter
@Setter
public class ConsentResponseDTO {
    private final String code;
    private final String state;
    private final String basePath;
    private final String scope;

    public ConsentResponseDTO(ConsentResponse response) {
        this.code = response.getCode().getValue();
        this.state = response.getState();
        this.basePath = response.getRedirectUri();
        this.scope = response.getCode().getUserData().getScope();
    }

    public String toUrl() {
        return UriComponentsBuilder.fromUriString(basePath)
                .queryParam("code", code)
                .queryParam("state", state)
                .queryParam("scope", scope)
                .toUriString();
    }
}
