package com.zpi.api.token;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.zpi.domain.token.TokenRequest;
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
    private String clientId;
    private String scope;

    public TokenRequest toDomain() {
        return TokenRequest.builder()
                .grantType(grantType)
                .code(code)
                .clientId(clientId)
                .scope(scope)
                .build();
    }
}

