package com.zpi.domain.token.ticketRequest.request;

import com.zpi.domain.client.Client;
import com.zpi.domain.common.RequestError;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

@NoArgsConstructor
@Component
public class RequestValidator {
    private static final HashSet<String> supportedResponseTypes = new HashSet<>(Collections.singleton("code"));

    private Request request;
    private Client client;

    public void validate(Request request, Client client) throws ValidationFailedException {
        this.request = request;
        this.client = client;

        validateClient();
        validateRedirectUri();
        validateResponseType();
        validateRequiredParameters();
        validateScope();
    }

    private void validateClient() throws ValidationFailedException {
        if (isUnauthorizedClient()) {
            var error = RequestError.<RequestErrorType>builder()
                    .error(RequestErrorType.UNAUTHORIZED_CLIENT)
                    .errorDescription("Unauthorized client id")
                    .build();
            throw new ValidationFailedException(error);
        }
    }

    private boolean isUnauthorizedClient() {
        return client == null;
    }

    private void validateRedirectUri() throws ValidationFailedException {
        if (isInvalidRedirectUri()) {
            var error = RequestError.<RequestErrorType>builder()
                    .error(RequestErrorType.UNRECOGNIZED_REDIRECT_URI)
                    .errorDescription("Unrecognized redirect uri")
                    .build();
            throw new ValidationFailedException(error);
        }
    }

    private boolean isInvalidRedirectUri() {
        return !client.containsRedirectUri(request.getRedirectUri());
    }

    private void validateResponseType() throws ValidationFailedException {
        if (isUnsupportedResponseType()) {
            var error = RequestError.<RequestErrorType>builder()
                    .error(RequestErrorType.UNSUPPORTED_RESPONSE_TYPE)
                    .errorDescription("Unrecognized response type: " + request.getResponseType())
                    .build();
            throw new ValidationFailedException(error);
        }
    }

    private boolean isUnsupportedResponseType() {
        var responseType = request.getResponseType();
        return responseType == null || !supportedResponseTypes.contains(responseType);
    }

    private void validateScope() throws ValidationFailedException {
        if (isScopeInvalid(request.getScope())) {
            var error = RequestError.<RequestErrorType>builder()
                    .error(RequestErrorType.INVALID_SCOPE)
                    .errorDescription("Invalid scope")
                    .build();
            throw new ValidationFailedException(error);
        }
    }

    private static boolean isScopeInvalid(List<String> scope) {
        if (scope == null) {
            return true;
        }

        return !scope.contains("openid");
    }

    private void validateRequiredParameters() throws ValidationFailedException {
        var missing = missingRequiredParameters();

        if (missing.size() != 0) {
            var description = missingRequiredParametersDescription(missing);
            var error = RequestError.<RequestErrorType>builder()
                    .error(RequestErrorType.INVALID_REQUEST)
                    .errorDescription(description)
                    .build();
            throw new ValidationFailedException(error);
        }
    }

    private List<String> missingRequiredParameters() {
        var result = new ArrayList<String>();

        if (request.getState() == null || request.getState().equals("")) {
            result.add("state");
        }

        if (request.getClientId() == null || request.getClientId().equals("")) {
            result.add("client_id");
        }

        return result;
    }

    private static String missingRequiredParametersDescription(List<String> missing) {
        return "Missing: " + String.join(" ", missing);
    }
}
