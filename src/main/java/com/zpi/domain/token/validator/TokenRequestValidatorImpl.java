package com.zpi.domain.token.validator;

import com.zpi.domain.authCode.consentRequest.authCodePersister.AuthCodeRepository;
import com.zpi.domain.common.RequestError;
import com.zpi.domain.organization.client.ClientRepository;
import com.zpi.domain.token.TokenRepository;
import com.zpi.domain.token.RefreshRequest;
import com.zpi.domain.token.TokenRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class TokenRequestValidatorImpl implements TokenRequestValidator {
    private final ClientRepository clientRepository;
    private final AuthCodeRepository authCodeRepository;
    private final TokenRepository tokenRepository;

    @Override
    public void validate(TokenRequest request) throws ValidationFailedException {
        validateCode(request);
        validate(new ValidationRequest(request));
    }

    @Override
    public void validate(RefreshRequest request) throws ValidationFailedException {
        validateRefreshToken(request);
        validate(new ValidationRequest(request));
        validateScope(request);
    }

    private void validate(ValidationRequest request) throws ValidationFailedException {
        validateClientId(request);
        validateGrantType(request);
    }

    private void validateCode(TokenRequest request) throws ValidationFailedException {
        if (isInvalidCode(request)) {
            var error = RequestError.<TokenRequestErrorType>builder()
                    .error(TokenRequestErrorType.INVALID_GRANT)
                    .errorDescription(unrecognizedValueMsg("code", request.getCode()))
                    .build();

            throw new ValidationFailedException(error);
        }
    }

    private boolean isInvalidCode(TokenRequest request) {
        var stored = authCodeRepository.findByKey(request.getCode());
        return stored.isEmpty();
    }

    private void validateRefreshToken(RefreshRequest request) throws ValidationFailedException {
        if (isInvalidRefreshToken(request)) {
            var error = RequestError.<TokenRequestErrorType>builder()
                    .error(TokenRequestErrorType.INVALID_GRANT)
                    .errorDescription(unrecognizedValueMsg("refresh_token", request.getRefreshToken()))
                    .build();

            throw new ValidationFailedException(error);
        }
    }

    private boolean isInvalidRefreshToken(RefreshRequest request) {
        var stored = tokenRepository.findByKey(request.getRefreshToken());
        return stored.isEmpty();
    }

    private void validateGrantType(ValidationRequest request) throws ValidationFailedException {
        if (isNotSupportedGrantType(request)) {
            var error = RequestError.<TokenRequestErrorType>builder()
                    .error(TokenRequestErrorType.UNSUPPORTED_GRANT_TYPE)
                    .errorDescription(unrecognizedGrantTypeMsg(request))
                    .build();

            throw new ValidationFailedException(error);
        }

        if (!isClientAuthorizedToUseGrantType(request)) {
            var error = RequestError.<TokenRequestErrorType>builder()
                    .error(TokenRequestErrorType.UNAUTHORIZED_CLIENT)
                    .errorDescription("Client is not authorized to use provided grant_type")
                    .build();

            throw new ValidationFailedException(error);
        }
    }

    private boolean isNotSupportedGrantType(ValidationRequest request) {
        final String SUPPORTED_GRANT_TYPE = "authorization_code";

        var type = request.getGrantType();

        return type == null || !type.equals(SUPPORTED_GRANT_TYPE);
    }

    private boolean isClientAuthorizedToUseGrantType(ValidationRequest request) {
        var client = clientRepository.findByKey(request.getClientId());

        if (client.isEmpty()) {
            return true;
        }

        return client.get().getAvailableGrantTypes().contains(request.getGrantType());
    }


    private String unrecognizedGrantTypeMsg(ValidationRequest request) {
        return String.format("Unrecognized grant_type '%s'. Expected 'authorization_code'.", request.getGrantType());
    }

    private void validateClientId(ValidationRequest request) throws ValidationFailedException {
        if (isInvalidClientId(request)) {
            var clientId = request.getClientId();
            var error = RequestError.<TokenRequestErrorType>builder()
                    .error(TokenRequestErrorType.INVALID_CLIENT)
                    .errorDescription(unrecognizedValueMsg("client_id", clientId))
                    .build();

            throw new ValidationFailedException(error);
        }
    }

    private boolean isInvalidClientId(ValidationRequest request) {
        if (request.getClientId() == null || request.getClientId().isEmpty()) {
            return true;
        }

        return clientRepository.findByKey(request.getClientId()).isEmpty();
    }

    private String unrecognizedValueMsg(String name, String value) {
        return String.format("Unrecognized %s '%s'", name, value);
    }

    private void validateScope(RefreshRequest request) throws ValidationFailedException {
        if (isInvalidScope(request)) {
            var error = RequestError.<TokenRequestErrorType>builder()
                    .error(TokenRequestErrorType.INVALID_SCOPE)
                    .errorDescription(unrecognizedValueMsg("scope", request.getScope()))
                    .build();

            throw new ValidationFailedException(error);
        }

        if (isScopeExceedingRights(request)) {
            var error = RequestError.<TokenRequestErrorType>builder()
                    .error(TokenRequestErrorType.INVALID_SCOPE)
                    .errorDescription("Scope exceeds the scope granted by the resource owner")
                    .build();

            throw new ValidationFailedException(error);
        }
    }

    private boolean isInvalidScope(RefreshRequest request) {
        var scope = request.getScope();
        return scope == null || scope.isEmpty();
    }

    private boolean isScopeExceedingRights(RefreshRequest request) {
        var client = clientRepository.findByKey(request.getClientId());

        if (client.isEmpty()) {
            return true;
        }

        var scope = request.getScope().split(" ");
        return Arrays.stream(scope).anyMatch(s -> !client.get().getHardcodedDefaultScope().contains(s));
    }
}
