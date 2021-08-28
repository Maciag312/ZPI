package com.zpi.domain.token.tokenRequest.requestValidator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TokenRequestErrorType {
    INVALID_GRANT_TYPE("INVALID_GRANT_TYPE"),

    INVALID_CODE("INVALID_CODE"),

    UNRECOGNIZED_CLIENT_ID("UNRECOGNIZED_CLIENT_ID"),

    UNRECOGNIZED_REDIRECT_URI("UNRECOGNIZED_REDIRECT_URI");

    private final String name;
}
