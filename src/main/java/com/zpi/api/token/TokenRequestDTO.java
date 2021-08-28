package com.zpi.api.token;

import com.zpi.domain.token.tokenRequest.TokenRequest;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenRequestDTO {
    private final String grant_type;
    private final String code;
    private final String redirect_uri;
    private final String client_id;

    public TokenRequest toDomain() {
        return TokenRequest.builder()
                .grantType(grant_type)
                .code(code)
                .redirectUri(redirect_uri)
                .clientId(client_id)
                .build();
    }
}

