package com.zpi.domain.authCode.authorizationRequest;

import com.zpi.domain.authCode.authenticationRequest.Request;

public interface AuthorizationService {
    AuthorizationResponse createTicket(Request request);
}
