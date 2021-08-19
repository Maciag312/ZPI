package com.zpi.domain.authCode.authenticationRequest;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class Request {
    private final String clientId;
    private final String redirectUri;
    private final String responseType;
    private final List<String> scope;
    private final String state;
}
