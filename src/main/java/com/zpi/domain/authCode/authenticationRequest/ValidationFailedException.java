package com.zpi.domain.authCode.authenticationRequest;

import com.zpi.domain.common.RequestError;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ValidationFailedException extends Exception {
    private final RequestError<AuthenticationRequestErrorType> error;
}
