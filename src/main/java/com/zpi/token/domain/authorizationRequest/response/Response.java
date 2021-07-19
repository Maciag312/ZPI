package com.zpi.token.domain.authorizationRequest.response;

import com.zpi.token.domain.authorizationRequest.request.Request;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
public class Response {
    private final String code;
    private final String state;

    public Response(Request request) {
        this.code = generateCode();
        this.state = request.getState();
    }

    private static String generateCode() {
        return UUID.randomUUID().toString();
    }
}
