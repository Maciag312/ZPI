package com.zpi.api.token;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.zpi.domain.token.tokenRequest.TokenRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TokenRequestDTO {
    private String grantType;
    private String code;
    private String redirectUri;
    private String clientId;

    public TokenRequest toDomain() {
        return TokenRequest.builder()
                .grantType(grantType)
                .code(code)
                .redirectUri(redirectUri)
                .clientId(clientId)
                .build();
    }
}

