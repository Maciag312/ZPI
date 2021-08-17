package com.zpi.domain.token;

import com.zpi.domain.client.ClientRepository;
import com.zpi.api.token.authorizationRequest.ErrorResponseDTO;
import com.zpi.api.token.authorizationRequest.ErrorResponseException;
import com.zpi.api.token.authorizationRequest.ResponseDTO;
import com.zpi.domain.token.ticketRequest.request.*;
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

    public ResponseDTO authenticationTicket(User user, Request request) throws ErrorResponseException {
        var client = clientRepository.getByKey(request.getClientId());

        try {
            requestValidator.validate(request, client.orElse(null));
        } catch (InvalidRequestException e) {
            var error = new ErrorResponseDTO(e.error, request.getState());
            throw new ErrorResponseException(error);
        }

        if (userAuthenticator.isAuthenticated(user)) {
            var response = Response.createTicket(request);
            return new ResponseDTO(response);
        }

        var error = RequestError.builder()
                .error(RequestErrorType.USER_AUTH_FAILED)
                .errorDescription("User authentication failed")
                .build();
        var errorResponse = new ErrorResponseDTO(error, request.getState());

        throw new ErrorResponseException(errorResponse);
    }
}
