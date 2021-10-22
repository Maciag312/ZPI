package com.zpi.api.token.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.zpi.domain.token.TokenRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TokenRequestDTO {
    @NotNull
    private String grantType;

    @NotNull
    private String code;

    @NotNull
    private String clientId;

    @NotNull
    private String redirectUri;

    public TokenRequest toDomain() {
        return TokenRequest.builder()
                .grantType(grantType)
                .code(code)
                .clientId(clientId)
                .redirectUri(redirectUri)
                .build();
    }
}

