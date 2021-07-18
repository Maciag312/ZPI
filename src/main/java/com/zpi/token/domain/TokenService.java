package com.zpi.token.domain;

import com.zpi.token.api.authorizationRequest.ErrorResponseDTO;
import com.zpi.token.api.authorizationRequest.RequestDTO;
import com.zpi.token.api.authorizationRequest.ResponseDTO;
import com.zpi.token.domain.authorizationRequest.request.InvalidRequestException;
import com.zpi.token.domain.authorizationRequest.request.RequestValidation;
import com.zpi.token.domain.authorizationRequest.response.ResponseService;
import com.zpi.user.domain.EndUserService;
import com.zpi.utils.BasicAuth;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TokenService {
    private final WebClientRepository clientRepository;
    private final EndUserService userService;

    public ResponseEntity<?> validateAuthorizationRequest(RequestDTO requestDTO, BasicAuth auth) {
        var request = requestDTO.toDomain();
        var client = clientRepository.getByKey(request.getClientId());

        var validator = new RequestValidation(request, client.orElse(null), userService);

        try {
            validator.validate(auth);
        } catch (InvalidRequestException e) {
            return new ResponseEntity<>(new ErrorResponseDTO(e.error), e.status);
        }

        var response = ResponseService.response(request);
        return new ResponseEntity<>(new ResponseDTO(response), HttpStatus.OK);
    }
}
