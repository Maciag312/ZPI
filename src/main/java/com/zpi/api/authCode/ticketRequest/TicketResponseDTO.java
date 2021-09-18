package com.zpi.api.authCode.ticketRequest;

import com.zpi.domain.authCode.authorizationRequest.AuthorizationResponse;
import lombok.Value;

import java.util.Optional;

@Value
public class TicketResponseDTO {
    Optional<String> ticket;
    Optional<String> state;

    public TicketResponseDTO(AuthorizationResponse response) {
        this.ticket = Optional.ofNullable(response.getTicket());
        this.state = Optional.ofNullable(response.getState());
    }
}
