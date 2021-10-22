package com.zpi.api.authCode.authenticationRequest;

import com.zpi.domain.authCode.authenticationRequest.AuthenticationRequest;
import lombok.Getter;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Getter
public class AuthenticationResponseDTO {
    private final String clientId;
    private final String redirectUri;
    private final String responseType;
    private final List<String> scope;
    private final String state;

    public AuthenticationResponseDTO(AuthenticationRequest request) {
        this.clientId = request.getClientId();
        this.redirectUri = request.getRedirectUri();
        this.responseType = request.getResponseType();
        this.scope = request.getScope();
        this.state = request.getState();
    }

    public String toUrl(String basePath) {
        return UriComponentsBuilder.fromUriString(basePath)
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", responseType)
                .queryParam("scope", scope)
                .queryParam("state", state)
                .toUriString();
    }
}
