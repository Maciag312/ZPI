package com.zpi.api.authCode.ticketRequest;

import com.zpi.domain.authCode.authorizationRequest.AuthorizationResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TicketResponseDTO {
    private final String ticket;
    private final String state;

    public TicketResponseDTO(AuthorizationResponse response) {
        this.ticket = response.getTicket();
        this.state = response.getState();
    }
}
