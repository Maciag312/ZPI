package com.zpi.token.domain;

import com.zpi.token.api.authorizationRequest.RequestDTO;
import com.zpi.token.api.authorizationRequest.RequestErrorDTO;
import com.zpi.token.domain.authorizationRequest.RequestValidation;
import com.zpi.token.domain.authorizationRequest.InvalidRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TokenService {
    private final WebClientRepository clientRepository;

    public ResponseEntity<?> validateAuthorizationRequest(RequestDTO requestDTO) {
        var request = requestDTO.toDomain();
        var client = clientRepository.getByKey(request.getClientId());

        var validator = new RequestValidation(request, client.orElse(null));

        try {
            validator.validate();
        } catch (InvalidRequestException e) {
            return new ResponseEntity<>(new RequestErrorDTO(e.error), e.status);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
