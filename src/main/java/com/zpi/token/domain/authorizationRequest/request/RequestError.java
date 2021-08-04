package com.zpi.token.domain.authorizationRequest.request;

import lombok.*;

@Getter
@Builder
@EqualsAndHashCode
public class RequestError {
    private final RequestErrorType error;
    private final String errorDescription;
}
