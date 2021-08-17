package com.zpi.domain.token.ticketRequest.request;

import lombok.*;

@Getter
@Builder
@EqualsAndHashCode
public class RequestError {
    private final RequestErrorType error;
    private final String errorDescription;
}
