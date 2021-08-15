package com.zpi.domain.token.ticketRequest.response;

import com.zpi.domain.token.ticketRequest.request.Request;
import lombok.Getter;

import java.util.UUID;

@Getter
public class Response {
    private final String ticket;
    private final String state;

    private Response(Request request) {
        this.ticket = generateTicket();
        this.state = request.getState();
    }

    public static Response createTicket(Request request) {
        return new Response(request);
    }

    private static String generateTicket() {
        return UUID.randomUUID().toString();
    }
}
