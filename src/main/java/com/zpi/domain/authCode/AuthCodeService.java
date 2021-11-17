package com.zpi.domain.authCode;

import com.zpi.api.common.exception.ErrorResponseException;
import com.zpi.domain.authCode.authenticationRequest.AuthenticationRequest;
import com.zpi.domain.authCode.authorizationRequest.TicketResponse;
import com.zpi.domain.authCode.consentRequest.ConsentRequest;
import com.zpi.domain.authCode.consentRequest.ConsentResponse;
import com.zpi.domain.authCode.consentRequest.ErrorConsentResponseException;
import com.zpi.domain.rest.ams.User;
import com.zpi.domain.rest.analysis.twoFactor.AnalysisRequest;

public interface AuthCodeService {
    AuthenticationRequest validateAndFillRequest(AuthenticationRequest request) throws ErrorResponseException;

    TicketResponse authenticationTicket(User user, AuthenticationRequest request, AnalysisRequest analysisRequest) throws ErrorResponseException;

    ConsentResponse consentRequest(ConsentRequest request) throws ErrorConsentResponseException;
}
