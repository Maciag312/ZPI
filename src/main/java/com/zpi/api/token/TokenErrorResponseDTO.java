package com.zpi.api.token;

import com.zpi.domain.token.tokenRequest.TokenErrorResponse;
import lombok.Getter;

@Getter
public class TokenErrorResponseDTO {
    private final String error;
    private final String error_description;

    public TokenErrorResponseDTO(TokenErrorResponse error) {
        this.error = error.getError();
        this.error_description = error.getErrorDescription();
    }
}
