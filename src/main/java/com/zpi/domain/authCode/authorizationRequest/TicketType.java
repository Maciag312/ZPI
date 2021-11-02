package com.zpi.domain.authCode.authorizationRequest;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TicketType {
    TICKET_2FA("TICKET_2FA"),

    TICKET("TICKET");

    private final String name;
}
