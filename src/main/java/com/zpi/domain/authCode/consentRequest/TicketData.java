package com.zpi.domain.authCode.consentRequest;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TicketData {
    private final String redirectUri;
    private final String scope;
}
