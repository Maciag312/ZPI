package com.zpi.domain.token;

import com.zpi.api.common.dto.ErrorResponseDTO;
import com.zpi.api.common.exception.ErrorResponseException;
import com.zpi.api.token.ticketRequest.ResponseDTO;
import com.zpi.domain.client.ClientRepository;
import com.zpi.domain.common.RequestError;
import com.zpi.domain.token.consentRequest.ConsentRequest;
import com.zpi.domain.token.consentRequest.ConsentResponse;
import com.zpi.domain.token.consentRequest.ConsentService;
import com.zpi.domain.token.consentRequest.ErrorConsentResponseException;
import com.zpi.domain.token.ticketRequest.request.ValidationFailedException;
import com.zpi.domain.token.ticketRequest.request.Request;
import com.zpi.domain.token.ticketRequest.request.RequestErrorType;
import com.zpi.domain.token.ticketRequest.request.RequestValidator;
import com.zpi.domain.token.ticketRequest.response.Response;
import com.zpi.domain.user.User;
import com.zpi.domain.user.UserAuthenticator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TokenService {
    private final ClientRepository clientRepository;
    private final RequestValidator requestValidator;
    private final UserAuthenticator userAuthenticator;
    private final ConsentService consentService;

    public ResponseDTO authenticationTicket(User user, Request request) throws ErrorResponseException {
        var client = clientRepository.getByKey(request.getClientId());

        try {
            requestValidator.validate(request, client.orElse(null));
        } catch (ValidationFailedException e) {
            var error = new ErrorResponseDTO(e.getError(), request.getState());
            throw new ErrorResponseException(error);
        }

        if (userAuthenticator.isAuthenticated(user)) {
            var response = Response.createTicket(request);
            return new ResponseDTO(response);
        }

        var error = RequestError.<RequestErrorType>builder()
                .error(RequestErrorType.USER_AUTH_FAILED)
                .errorDescription("User authentication failed")
                .build();

        var errorResponse = new ErrorResponseDTO(error, request.getState());

        throw new ErrorResponseException(errorResponse);
    }

    public ConsentResponse consentRequest(ConsentRequest request) throws ErrorConsentResponseException {
        var response = consentService.consent(request);

        return ConsentResponse.builder()
                .code(response)
                .state(request.getState())
                .build();
    }
}
