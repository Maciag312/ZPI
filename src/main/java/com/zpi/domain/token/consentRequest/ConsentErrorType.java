package com.zpi.domain.token.consentRequest;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ConsentErrorType {
    TICKET_EXPIRED("TICKET_EXPIRED");

    private final String name;
}
