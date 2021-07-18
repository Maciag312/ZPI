package com.zpi.token.domain.authorizationRequest.response;

import com.zpi.token.domain.authorizationRequest.request.Request;

import java.util.UUID;

public class ResponseService {
    public static Response response(Request request) {
        return Response.builder()
                .code(generateCode())
                .state(request.getState())
                .build();
    }

    private static String generateCode() {
        return UUID.randomUUID().toString();
    }
}
