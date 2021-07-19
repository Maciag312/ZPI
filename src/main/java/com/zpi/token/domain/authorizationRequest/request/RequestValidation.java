package com.zpi.token.domain.authorizationRequest.request;

import com.zpi.token.domain.Client;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.*;

@AllArgsConstructor
public class RequestValidation {
    private static final HashSet<String> supportedResponseTypes = new HashSet<>(Collections.singleton("code"));

    @Getter
    @AllArgsConstructor
    private static class RequestValidationParams {
        private final Request request;
        private final Client client;
    }

    public static void validate(Request request, Client client) throws InvalidRequestException {
        var params = new RequestValidationParams(request, client);

        validateClient(params);
        validateRedirectUri(params);
        validateResponseType(params);
        validateScope(params);
        validateRequiredParameters(params);
    }

    private static void validateClient(RequestValidationParams params) throws InvalidRequestException {
        if (isUnauthorizedClient(params)) {
            var error = RequestError.builder()
                    .error(RequestErrorType.UNAUTHORIZED_CLIENT)
                    .errorDescription("Unauthorized client id")
                    .build();
            throw new InvalidRequestException(HttpStatus.BAD_REQUEST, error);
        }
    }

    private static boolean isUnauthorizedClient(RequestValidationParams params) {
        return params.client == null;
    }

    private static void validateRedirectUri(RequestValidationParams params) throws InvalidRequestException {
        if (isInvalidRedirectUri(params)) {
            var error = RequestError.builder()
                    .error(RequestErrorType.UNRECOGNIZED_REDIRECT_URI)
                    .errorDescription("Unrecognized redirect uri")
                    .build();
            throw new InvalidRequestException(HttpStatus.BAD_REQUEST, error);
        }
    }

    private static boolean isInvalidRedirectUri(RequestValidationParams params) {
        return !params.client.containsRedirectUri(params.request.getRedirectUri());
    }

    private static void validateResponseType(RequestValidationParams params) throws InvalidRequestException {
        if (isUnsupportedResponseType(params)) {
            var error = RequestError.builder()
                    .error(RequestErrorType.UNSUPPORTED_RESPONSE_TYPE)
                    .errorDescription("Unrecognized response type: " + params.request.getResponseType())
                    .build();
            throw new InvalidRequestException(HttpStatus.BAD_REQUEST, error);
        }
    }

    private static boolean isUnsupportedResponseType(RequestValidationParams params) {
        var responseType = params.request.getResponseType();
        return responseType == null || !supportedResponseTypes.contains(responseType);
    }

    private static void validateScope(RequestValidationParams params) throws InvalidRequestException {
        if (isScopeInvalid(params.request.getScope())) {
            var error = RequestError.builder()
                    .error(RequestErrorType.INVALID_SCOPE)
                    .errorDescription("Invalid scope")
                    .build();
            throw new InvalidRequestException(HttpStatus.BAD_REQUEST, error);
        }
    }

    private static boolean isScopeInvalid(String scope) {
        if (scope == null) {
            return true;
        }

        var scopeValues = scope.split(" ");

        return !Arrays.asList(scopeValues).contains("openid");
    }

    private static void validateRequiredParameters(RequestValidationParams params) throws InvalidRequestException {
        var missing = missingRequiredParameters(params);

        if (missing.size() != 0) {
            var description = missingRequiredParametersDescription(missing);
            var error = RequestError.builder()
                    .error(RequestErrorType.INVALID_REQUEST)
                    .errorDescription(description)
                    .build();
            throw new InvalidRequestException(HttpStatus.BAD_REQUEST, error);
        }
    }

    private static String missingRequiredParametersDescription(List<String> missing) {
        return "Missing: " + String.join(" ", missing);
    }

    private static List<String> missingRequiredParameters(RequestValidationParams params) {
        var result = new ArrayList<String>();

        if (params.request.getState() == null) {
            result.add("state");
        }

        if (params.request.getClientId() == null) {
            result.add("client_id");
        }

        return result;
    }
}
