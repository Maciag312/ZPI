package com.zpi.domain.token;

import com.zpi.domain.token.issuer.TokenIssuer;
import com.zpi.domain.token.validator.TokenRequestValidator;
import com.zpi.domain.token.validator.ValidationFailedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

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
        } catch (ValidationFailedException | NoSuchElementException e) {
            throwTokenException(e);
        }

        return null;
    }

    private void throwTokenException(Exception e) throws TokenErrorResponseException {
        String error = "";
        String description = "";
        if (e instanceof ValidationFailedException) {
            var ex = (ValidationFailedException) e;
            error = ex.getError().getError().toString();
            description = ex.getError().getErrorDescription();
        } else {
            error = e.getMessage();
            description = e.getLocalizedMessage();
        }

        throw new TokenErrorResponseException(TokenErrorResponse.builder()
                .error(error)
                .errorDescription(description)
                .build());
    }

    @Override
    public Token refreshToken(RefreshRequest request) throws TokenErrorResponseException {
        try {
            validator.validate(request);
            return issuer.refresh(request);
        } catch (ValidationFailedException | NoSuchElementException e) {
            throwTokenException(e);
        }

        return null;
    }
}
