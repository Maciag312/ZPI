package com.zpi.token.domain;

import com.zpi.client.domain.ClientRepository;
import com.zpi.token.api.authorizationRequest.ErrorResponseDTO;
import com.zpi.token.api.authorizationRequest.ErrorResponseException;
import com.zpi.token.api.authorizationRequest.ResponseDTO;
import com.zpi.token.domain.authorizationRequest.request.*;
import com.zpi.token.domain.authorizationRequest.response.Response;
import com.zpi.user.domain.User;
import com.zpi.user.domain.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TokenService {
    private final ClientRepository clientRepository;
    private final RequestValidation requestValidation;
    private final UserService userService;

    public ResponseDTO authorizationRequest(User user, Request request) throws ErrorResponseException {
        var client = clientRepository.getByKey(request.getClientId());

        try {
            requestValidation.validate(request, client.orElse(null));
        } catch (InvalidRequestException e) {
            var error = new ErrorResponseDTO(e.error, request.getState());
            throw new ErrorResponseException(error);
        }

        if (userService.isAuthenticated(user)) {
            var response = new Response(request);
            return new ResponseDTO(response);
        }

        var error = new ErrorResponseDTO(RequestError.builder().error(RequestErrorType.USER_AUTH_FAILED).errorDescription("User authentication failed").build(), request.getState());
        throw new ErrorResponseException(error);
    }
}
