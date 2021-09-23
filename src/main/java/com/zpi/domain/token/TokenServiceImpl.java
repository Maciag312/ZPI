package com.zpi.domain.token;

import com.zpi.domain.token.refreshRequest.RefreshRequest;
import com.zpi.domain.token.tokenRequest.Token;
import com.zpi.domain.token.tokenRequest.TokenErrorResponse;
import com.zpi.domain.token.tokenRequest.TokenRequest;
import com.zpi.domain.token.tokenRequest.requestValidator.TokenRequestValidator;
import com.zpi.domain.token.tokenRequest.requestValidator.ValidationFailedException;
import com.zpi.domain.token.tokenRequest.tokenIssuer.TokenIssuer;
import com.zpi.domain.token.tokenRequest.tokenIssuer.TokenIssuerFailedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {
    private final TokenIssuer issuer;
    private final TokenRequestValidator validator;

    @Override
    public Token getToken(TokenRequest request) throws TokenErrorResponseException {
        try {
            validator.validate(request);
            return issuer.issue(request);
        } catch (ValidationFailedException | TokenIssuerFailedException e) {
            throwTokenException(e);
        }

        return null;
    }

    private void throwTokenException(Exception e) throws TokenErrorResponseException {
        String error = "";
        String description = "";
        if (e instanceof TokenIssuerFailedException) {
            var ex = (TokenIssuerFailedException) e;
            error = ex.getError().getError().toString();
            description = ex.getError().getErrorDescription();
        } else if (e instanceof ValidationFailedException) {
            var ex = (ValidationFailedException) e;
            error = ex.getError().getError().toString();
            description = ex.getError().getErrorDescription();
        }

        throw new TokenErrorResponseException(TokenErrorResponse.builder()
                .error(error)
                .errorDescription(description)
                .build());
    }

    @Override
    public Token refreshToken(RefreshRequest request) throws TokenErrorResponseException {
        try {
            return issuer.refresh(request);
        } catch (TokenIssuerFailedException e) {
            throwTokenException(e);
        }

        return null;
    }
}
