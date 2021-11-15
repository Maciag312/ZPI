package com.zpi.domain.authCode.authorizationRequest;

import com.zpi.domain.authCode.authenticationRequest.AuthenticationRequest;
import com.zpi.domain.rest.ams.User;
import com.zpi.domain.rest.analysis.afterLogin.AnalysisRequest;

public interface AuthorizationService {
    AuthorizationResponse createTicket(User user, AuthenticationRequest request, AnalysisRequest analysisRequest);
}
