package com.zpi.token.domain.authorizationRequest;

import lombok.*;

@EqualsAndHashCode
@Getter
@Builder
public class RequestError {
    private final RequestErrorType error;
    private final String errorDescription;
}
