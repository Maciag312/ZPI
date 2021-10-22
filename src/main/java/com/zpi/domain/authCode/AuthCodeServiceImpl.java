package com.zpi.domain.authCode;

import com.zpi.api.common.dto.ErrorResponseDTO;
import com.zpi.api.common.exception.ErrorResponseException;
import com.zpi.domain.audit.AuditMetadata;
import com.zpi.domain.audit.AuditService;
import com.zpi.domain.authCode.authenticationRequest.AuthenticationRequest;
import com.zpi.domain.authCode.authenticationRequest.AuthenticationRequestErrorType;
import com.zpi.domain.authCode.authenticationRequest.RequestValidator;
import com.zpi.domain.authCode.authenticationRequest.ValidationFailedException;
import com.zpi.domain.authCode.authorizationRequest.AuthorizationResponse;
import com.zpi.domain.authCode.authorizationRequest.AuthorizationService;
import com.zpi.domain.authCode.consentRequest.ConsentRequest;
import com.zpi.domain.authCode.consentRequest.ConsentResponse;
import com.zpi.domain.authCode.consentRequest.ConsentService;
import com.zpi.domain.authCode.consentRequest.ErrorConsentResponseException;
import com.zpi.domain.common.RequestError;
import com.zpi.domain.rest.ams.AmsService;
import com.zpi.domain.rest.ams.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthCodeServiceImpl implements AuthCodeService {
    private final RequestValidator requestValidator;
    private final AmsService ams;
    private final ConsentService consentService;
    private final AuthorizationService authorizationService;
    private final AuditService auditService;

    public AuthenticationRequest validateAndFillRequest(AuthenticationRequest request) throws ErrorResponseException {
        try {
            return requestValidator.validateAndFillMissingFields(request);
        } catch (ValidationFailedException e) {
            var error = new ErrorResponseDTO<>(e.getError(), request.getState());
            throw new ErrorResponseException(error);
        }
    }

    public AuthorizationResponse authenticationTicket(User user, AuthenticationRequest request, AuditMetadata metadata) throws ErrorResponseException {
        validateAndFillRequest(request);
        auditService.audit(user, request, metadata);
        validateUser(user, request);

        return authorizationService.createTicket(user, request);
    }

    private void validateUser(User user, AuthenticationRequest request) throws ErrorResponseException {
        if (!ams.isAuthenticated(user)) {
            var error = RequestError.<AuthenticationRequestErrorType>builder()
                    .error(AuthenticationRequestErrorType.USER_AUTH_FAILED)
                    .errorDescription("User authentication failed")
                    .build();

            var errorResponse = new ErrorResponseDTO<>(error, request.getState());

            throw new ErrorResponseException(errorResponse);
        }
    }

    public ConsentResponse consentRequest(ConsentRequest request) throws ErrorConsentResponseException {
        return consentService.consent(request);
    }
}
