package com.zpi.domain.authCode.authenticationRequest;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthenticationRequestErrorType {
    UNAUTHORIZED_CLIENT("UNAUTHORIZED_CLIENT"),
    INVALID_REQUEST("INVALID_REQUEST"),
    UNSUPPORTED_RESPONSE_TYPE("UNSUPPORTED_RESPONSE_TYPE"),
    INVALID_SCOPE("INVALID_SCOPE"),
    UNRECOGNIZED_REDIRECT_URI("UNRECOGNIZED_REDIRECT_URI"),
    USER_AUTH_FAILED("USER_AUTH_FAILED"),
    LOGIN_LOCKOUT("LOGIN_LOCKOUT"),
    ANALYSIS_NOT_AVAILABLE("ANALYSIS_NOT_AVAILABLE");

    private final String name;
}
