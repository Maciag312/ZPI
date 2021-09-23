package com.zpi.domain.authCode.consentRequest;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class TicketData {
    private final String redirectUri;
    private final String scope;
    private final String username;

    public TicketData(String redirectUri, List<String> scope, String username) {
        this.redirectUri = redirectUri;
        this.scope = String.join(" ", scope);
        this.username = username;
    }
}
