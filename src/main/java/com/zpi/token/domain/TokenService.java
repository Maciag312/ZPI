package com.zpi.token.domain;

import com.zpi.client.domain.ClientRepository;
import com.zpi.common.api.dto.UserDTO;
import com.zpi.token.api.authorizationRequest.ErrorResponseDTO;
import com.zpi.token.api.authorizationRequest.RequestDTO;
import com.zpi.token.api.authorizationRequest.ResponseDTO;
import com.zpi.token.domain.authorizationRequest.request.InvalidRequestException;
import com.zpi.token.domain.authorizationRequest.request.RequestError;
import com.zpi.token.domain.authorizationRequest.request.RequestErrorType;
import com.zpi.token.domain.authorizationRequest.request.RequestValidation;
import com.zpi.token.domain.authorizationRequest.response.Response;
import com.zpi.user.domain.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TokenService {
    private final ClientRepository clientRepository;
    private final RequestValidation requestValidation;
    private final UserService userService;

    private static final HttpStatus status = HttpStatus.FOUND;

    public ResponseEntity<?> authorizationRequest(UserDTO user, RequestDTO requestDTO) {
        var request = requestDTO.toDomain();
        var client = clientRepository.getByKey(request.getClientId());

        try {
            requestValidation.validate(request, client.orElse(null));
        } catch (InvalidRequestException e) {
            var error = new ErrorResponseDTO(e.error, request.getState());
            return new ResponseEntity<>(error, e.status);
        }

        if (userService.isAuthenticated(user)) {
            var response = new Response(request);
            return new ResponseEntity<>(new ResponseDTO(response), status);
        }

        var error = new ErrorResponseDTO(RequestError.builder().error(RequestErrorType.USER_AUTH_FAILED).errorDescription("User authentication failed").build(), request.getState());
        return new ResponseEntity<>(error, status);
    }
}
