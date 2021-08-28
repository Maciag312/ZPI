package com.zpi.domain.authCode.authorizationRequest;

import com.zpi.domain.authCode.authenticationRequest.AuthenticationRequest;

public interface AuthorizationService {
    AuthorizationResponse createTicket(AuthenticationRequest request);
}
