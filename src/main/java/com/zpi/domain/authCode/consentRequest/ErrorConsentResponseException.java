package com.zpi.domain.authCode.consentRequest;

import com.zpi.domain.common.RequestError;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.util.UriComponentsBuilder;

@RequiredArgsConstructor
@Getter
public class ErrorConsentResponseException extends Exception {
    private final RequestError<ConsentErrorType> error;

    public String toUrl(String basePath, String state) {
        return UriComponentsBuilder.fromUriString(basePath)
                .queryParam("error", error.getError())
                .queryParam("error_description", error.getErrorDescription())
                .queryParam("state", state)
                .toUriString();
    }
}
