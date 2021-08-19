package com.zpi.domain.authCode.consentRequest;

import lombok.Builder;
import lombok.Getter;
import org.springframework.web.util.UriComponentsBuilder;

@Getter
@Builder
public class ConsentResponse {
    private final AuthCode code;
    private final String state;
    private final String redirectUri;

    public String toUrl(String basePath) {
        return UriComponentsBuilder.fromUriString(basePath)
                .queryParam("code", code.getValue())
                .queryParam("state", state)
                .toUriString();
    }
}
