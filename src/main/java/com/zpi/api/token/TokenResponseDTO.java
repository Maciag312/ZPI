package com.zpi.api.token;

import com.zpi.domain.token.tokenRequest.Token;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class TokenResponseDTO {
    private final String access_token;
    private final String token_type;
    private final String refresh_token;
    private final String expires_in;

    public TokenResponseDTO(Token token) {
        access_token = token.getAccessToken();
        token_type = token.getTokenType();
        refresh_token = token.getRefreshToken();
        expires_in = token.getExpiresIn();
    }
}
