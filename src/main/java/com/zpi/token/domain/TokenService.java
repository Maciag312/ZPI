package com.zpi.token.domain;

import com.zpi.token.api.authorizationRequest.ErrorResponseDTO;
import com.zpi.token.api.authorizationRequest.RequestDTO;
import com.zpi.token.api.authorizationRequest.ResponseDTO;
import com.zpi.token.domain.authorizationRequest.request.InvalidRequestException;
import com.zpi.token.domain.authorizationRequest.request.RequestValidation;
import com.zpi.token.domain.authorizationRequest.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TokenService {
    private final ClientRepository clientRepository;

    public ResponseEntity<?> validateAuthorizationRequest(RequestDTO requestDTO) {
        var request = requestDTO.toDomain();
        var client = clientRepository.getByKey(request.getClientId());

        try {
            RequestValidation.validate(request, client.orElse(null));
        } catch (InvalidRequestException e) {
            var error = new ErrorResponseDTO(e.error, request.getState());
            return new ResponseEntity<>(error, e.status);
        }

        var response = new Response(request);
        return new ResponseEntity<>(new ResponseDTO(response), HttpStatus.OK);
    }
}
