package com.zpi.domain.token.validator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TokenRequestErrorType {
    UNSUPPORTED_GRANT_TYPE("UNSUPPORTED_GRANT_TYPE"),

    INVALID_GRANT("INVALID_GRANT"),

    INVALID_CLIENT("INVALID_CLIENT"),

    UNAUTHORIZED_CLIENT("UNAUTHORIZED_CLIENT"),

    INVALID_SCOPE("INVALID_SCOPE"),

    UNRECOGNIZED_REDIRECT_URI("UNRECOGNIZED_REDIRECT_URI");

    private final String name;
}
