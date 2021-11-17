package com.zpi.domain.authCode;

import com.zpi.api.common.dto.ErrorResponseDTO;
import com.zpi.api.common.exception.ErrorResponseException;
import com.zpi.domain.authCode.authenticationRequest.AuthenticationRequest;
import com.zpi.domain.authCode.authenticationRequest.RequestValidator;
import com.zpi.domain.authCode.authenticationRequest.ValidationFailedException;
import com.zpi.domain.authCode.authorizationRequest.TicketResponse;
import com.zpi.domain.authCode.authorizationRequest.TicketService;
import com.zpi.domain.authCode.authorizationRequest.LoginLockoutException;
import com.zpi.domain.authCode.authorizationRequest.UserValidationFailedException;
import com.zpi.domain.authCode.consentRequest.ConsentRequest;
import com.zpi.domain.authCode.consentRequest.ConsentResponse;
import com.zpi.domain.authCode.consentRequest.ConsentService;
import com.zpi.domain.authCode.consentRequest.ErrorConsentResponseException;
import com.zpi.domain.rest.ams.User;
import com.zpi.domain.rest.analysis.twoFactor.AnalysisRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthCodeServiceImpl implements AuthCodeService {
    private final RequestValidator requestValidator;
    private final ConsentService consentService;
    private final TicketService ticketService;

    public AuthenticationRequest validateAndFillRequest(AuthenticationRequest request) throws ErrorResponseException {
        try {
            return requestValidator.validateAndFillMissingFields(request);
        } catch (ValidationFailedException e) {
            var error = new ErrorResponseDTO<>(e.getError(), request.getState());
            throw new ErrorResponseException(error);
        }
    }

    public TicketResponse authenticationTicket(User user, AuthenticationRequest request, AnalysisRequest analysisRequest) throws ErrorResponseException {
        validateAndFillRequest(request);

        try {
            return ticketService.createTicket(user, request, analysisRequest);
        } catch (LoginLockoutException e) {
            var error = new ErrorResponseDTO<>(e.getError(), request.getState());
            throw new ErrorResponseException(error);
        } catch (UserValidationFailedException e ) {
            var error = new ErrorResponseDTO<>(e.getError(), request.getState());
            throw new ErrorResponseException(error);
        }
    }

    public ConsentResponse consentRequest(ConsentRequest request) throws ErrorConsentResponseException {
        return consentService.consent(request);
    }
}
