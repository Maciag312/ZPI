package com.zpi.domain.authCode;

import com.zpi.api.authCode.ticketRequest.ResponseDTO;
import com.zpi.api.common.dto.ErrorResponseDTO;
import com.zpi.api.common.exception.ErrorResponseException;
import com.zpi.domain.authCode.authenticationRequest.Request;
import com.zpi.domain.authCode.authenticationRequest.RequestErrorType;
import com.zpi.domain.authCode.authenticationRequest.RequestValidator;
import com.zpi.domain.authCode.authenticationRequest.ValidationFailedException;
import com.zpi.domain.authCode.authorizationRequest.AuthorizationService;
import com.zpi.domain.authCode.consentRequest.ConsentRequest;
import com.zpi.domain.authCode.consentRequest.ConsentResponse;
import com.zpi.domain.authCode.consentRequest.ConsentService;
import com.zpi.domain.authCode.consentRequest.ErrorConsentResponseException;
import com.zpi.domain.common.RequestError;
import com.zpi.domain.user.User;
import com.zpi.domain.user.UserAuthenticator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthCodeServiceImpl implements AuthCodeService {
    private final RequestValidator requestValidator;
    private final UserAuthenticator userAuthenticator;
    private final ConsentService consentService;
    private final AuthorizationService authorizationService;

    public void validateRequest(Request request) throws ErrorResponseException {
        try {
            requestValidator.validate(request);
        } catch (ValidationFailedException e) {
            var error = new ErrorResponseDTO(e.getError(), request.getState());
            throw new ErrorResponseException(error);
        }
    }

    public ResponseDTO authenticationTicket(User user, Request request) throws ErrorResponseException {
        validateRequest(request);
        validateUser(user, request);

        var response = authorizationService.createTicket(request);
        return new ResponseDTO(response);
    }

    private void validateUser(User user, Request request) throws ErrorResponseException {
        if (!userAuthenticator.isAuthenticated(user)) {
            var error = RequestError.<RequestErrorType>builder()
                    .error(RequestErrorType.USER_AUTH_FAILED)
                    .errorDescription("User authentication failed")
                    .build();

            var errorResponse = new ErrorResponseDTO(error, request.getState());

            throw new ErrorResponseException(errorResponse);
        }
    }

    public ConsentResponse consentRequest(ConsentRequest request) throws ErrorConsentResponseException {
        return consentService.consent(request);
    }
}
