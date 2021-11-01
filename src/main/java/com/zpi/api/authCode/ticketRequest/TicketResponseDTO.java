package com.zpi.api.authCode.ticketRequest;

import com.zpi.domain.authCode.authorizationRequest.AuthorizationResponse;
import lombok.Getter;

@Getter
public class TicketResponseDTO {
    private final String ticket;
    private final String ticket_type;
    private final String state;

    public TicketResponseDTO(AuthorizationResponse response) {
        this.ticket = response.getTicket();
        this.ticket_type = response.getTicketType().getName();
        this.state = response.getState();
    }
}
