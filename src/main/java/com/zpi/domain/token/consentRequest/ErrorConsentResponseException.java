package com.zpi.domain.token.consentRequest;

import com.zpi.domain.common.RequestError;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ErrorConsentResponseException extends Exception{
    private final RequestError<ConsentErrorType> error;
}
