package com.zpi.api.authCode.ticketRequest;

import com.zpi.domain.authCode.authorizationRequest.AuthorizationResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseDTO {
    private final String ticket;
    private final String state;

    public ResponseDTO(AuthorizationResponse response) {
        this.ticket = response.getTicket();
        this.state = response.getState();
    }
}
