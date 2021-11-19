package com.zpi.domain.authCode.authorizationRequest;

import com.zpi.domain.authCode.authenticationRequest.AuthenticationRequestErrorType;
import com.zpi.domain.common.RequestError;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserValidationFailedException extends Exception{
    private final RequestError<AuthenticationRequestErrorType> error;
}
