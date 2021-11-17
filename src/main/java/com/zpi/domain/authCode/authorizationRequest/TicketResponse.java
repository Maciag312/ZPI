package com.zpi.domain.authCode.authorizationRequest;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TicketResponse {
    private final String ticket;
    private final TicketType ticketType;
    private final String state;
}
