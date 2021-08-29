package com.zpi.domain.token.tokenRequest.requestValidator;

import com.zpi.domain.authCode.consentRequest.authCodeIssuer.AuthCodeRepository;
import com.zpi.domain.client.ClientRepository;
import com.zpi.domain.common.RequestError;
import com.zpi.domain.token.tokenRequest.TokenRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenRequestValidatorImpl implements TokenRequestValidator {
    private final ClientRepository clientRepository;
    private final AuthCodeRepository authCodeRepository;

    @Override
    public void validate(TokenRequest request) throws ValidationFailedException {
        validateGrantType(request);
        validateCode(request);
        validateClientId(request);
        validateRedirectUri(request);
    }

    private void validateGrantType(TokenRequest request) throws ValidationFailedException {
        if (isInvalidGrantType(request)) {
            var error = RequestError.<TokenRequestErrorType>builder()
                    .error(TokenRequestErrorType.INVALID_GRANT_TYPE)
                    .errorDescription(unrecognizedGrantTypeMsg(request))
                    .build();

            throw new ValidationFailedException(error);
        }
    }

    private boolean isInvalidGrantType(TokenRequest request) {
        final String SUPPORTED_GRANT_TYPE = "authorization_code";

        var type = request.getGrantType();
        return type == null || !type.equals(SUPPORTED_GRANT_TYPE);
    }

    private String unrecognizedGrantTypeMsg(TokenRequest request) {
        return String.format("Unrecognized grant_type '%s'. Expected 'authorization_code'.", request.getGrantType());
    }

    private void validateCode(TokenRequest request) throws ValidationFailedException {
        if (isInvalidCode(request)) {
            var code = request.getCode();
            var error = RequestError.<TokenRequestErrorType>builder()
                    .error(TokenRequestErrorType.INVALID_CODE)
                    .errorDescription(unrecognizedValueMsg("code", code))
                    .build();

            throw new ValidationFailedException(error);
        }
    }

    private boolean isInvalidCode(TokenRequest request) {
        var saved = authCodeRepository.getByKey(request.getCode());
        return saved.isEmpty();
    }


    private void validateClientId(TokenRequest request) throws ValidationFailedException {
        if (isInvalidClientId(request)) {
            var clientId = request.getClientId();
            var error = RequestError.<TokenRequestErrorType>builder()
                    .error(TokenRequestErrorType.UNRECOGNIZED_CLIENT_ID)
                    .errorDescription(unrecognizedValueMsg("client_id", clientId))
                    .build();

            throw new ValidationFailedException(error);
        }
    }

    private boolean isInvalidClientId(TokenRequest request) {
        if (request.getClientId() == null || request.getClientId().isEmpty()) {
            return true;
        }

        return clientRepository.getByKey(request.getClientId()).isEmpty();
    }

    private void validateRedirectUri(TokenRequest request) throws ValidationFailedException {
        if (isInvalidRedirectUri(request)) {
            var uri = request.getRedirectUri();
            var error = RequestError.<TokenRequestErrorType>builder()
                    .error(TokenRequestErrorType.UNRECOGNIZED_REDIRECT_URI)
                    .errorDescription(unrecognizedValueMsg("redirect_uri", uri))
                    .build();

            throw new ValidationFailedException(error);
        }
    }

    private boolean isInvalidRedirectUri(TokenRequest request) {
        if (request.getRedirectUri() == null || request.getRedirectUri().isEmpty()) {
            return true;
        }

        var client = clientRepository.getByKey(request.getClientId());

        if (client.isPresent()) {
            var uri = request.getRedirectUri();
            return !client.get().getAvailableRedirectUri().contains(uri);
        }

        return true;
    }

    private String unrecognizedValueMsg(String name, String value) {
        return String.format("Unrecognized %s '%s'", name, value);
    }
}
