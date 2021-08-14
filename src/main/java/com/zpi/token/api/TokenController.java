package com.zpi.token.api;

import com.zpi.common.api.dto.UserDTO;
import com.zpi.token.api.authorizationRequest.ErrorResponseException;
import com.zpi.token.api.authorizationRequest.RequestDTO;
import com.zpi.token.domain.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/token")
public class TokenController {
    private final TokenService tokenService;

    @PostMapping("/authorize")
    public ResponseEntity<?> authorize(@RequestBody UserDTO userDTO,
                                       @RequestParam String client_id,
                                       @RequestParam String redirect_uri,
                                       @RequestParam String response_type,
                                       @RequestParam String scope,
                                       @RequestParam String state) {

        var requestDTO = RequestDTO.builder()
                .clientId(client_id)
                .redirectUri(redirect_uri)
                .responseType(response_type)
                .scope(scope)
                .state(state)
                .build();

        var user = userDTO.toHashedDomain();
        var request = requestDTO.toDomain();

        try {
            var response = tokenService.authorizationRequest(user, request);
            return new ResponseEntity<>(response, HttpStatus.FOUND);
        } catch (ErrorResponseException e) {
            return new ResponseEntity<>(e.getErrorResponse(), HttpStatus.FOUND);
        }
    }
}
