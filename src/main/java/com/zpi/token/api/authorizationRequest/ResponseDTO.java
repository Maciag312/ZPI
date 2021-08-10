package com.zpi.token.api.authorizationRequest;

import com.zpi.token.domain.authorizationRequest.response.Response;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class ResponseDTO {
    private final String ticket;
    private final String state;

    public ResponseDTO(Response response) {
        this.ticket = response.getTicket();
        this.state = response.getState();
    }
}
