package com.zpi.token.domain.authorizationRequest.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Response {
    private final String code;
    private final String state;
}
