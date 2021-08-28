package com.zpi.domain.authCode;

import com.zpi.api.authCode.ticketRequest.TicketResponseDTO;
import com.zpi.api.common.exception.ErrorResponseException;
import com.zpi.domain.authCode.authenticationRequest.AuthenticationRequest;
import com.zpi.domain.authCode.consentRequest.ConsentRequest;
import com.zpi.domain.authCode.consentRequest.ConsentResponse;
import com.zpi.domain.authCode.consentRequest.ErrorConsentResponseException;
import com.zpi.domain.user.User;

public interface AuthCodeService {
    AuthenticationRequest validateAndFillRequest(AuthenticationRequest request) throws ErrorResponseException;

    TicketResponseDTO authenticationTicket(User user, AuthenticationRequest request) throws ErrorResponseException;

    ConsentResponse consentRequest(ConsentRequest request) throws ErrorConsentResponseException;
}
