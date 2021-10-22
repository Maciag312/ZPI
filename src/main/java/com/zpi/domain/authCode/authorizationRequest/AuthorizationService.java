package com.zpi.domain.authCode.authorizationRequest;

import com.zpi.domain.authCode.authenticationRequest.AuthenticationRequest;
import com.zpi.domain.rest.ams.User;

public interface AuthorizationService {
    AuthorizationResponse createTicket(User user, AuthenticationRequest request);
}
