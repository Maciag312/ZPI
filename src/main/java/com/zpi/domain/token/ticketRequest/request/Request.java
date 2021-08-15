package com.zpi.domain.token.ticketRequest.request;

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
