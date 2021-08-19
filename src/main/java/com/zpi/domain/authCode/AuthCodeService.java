package com.zpi.domain.authCode;

import com.zpi.api.authCode.ticketRequest.ResponseDTO;
import com.zpi.api.common.exception.ErrorResponseException;
import com.zpi.domain.authCode.consentRequest.ConsentRequest;
import com.zpi.domain.authCode.consentRequest.ConsentResponse;
import com.zpi.domain.authCode.consentRequest.ErrorConsentResponseException;
import com.zpi.domain.authCode.authenticationRequest.Request;
import com.zpi.domain.user.User;

public interface AuthCodeService {
    void validateRequest(Request request) throws ErrorResponseException;

    ResponseDTO authenticationTicket(User user, Request request) throws ErrorResponseException;

    ConsentResponse consentRequest(ConsentRequest request) throws ErrorConsentResponseException;
}
