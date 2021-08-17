package com.zpi.api.token;

import com.zpi.api.common.dto.UserDTO;
import com.zpi.api.token.authorizationRequest.ErrorResponseException;
import com.zpi.api.token.authorizationRequest.RequestDTO;
import com.zpi.domain.token.TokenService;
import com.zpi.domain.token.ticketRequest.request.Request;
import com.zpi.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
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

        var request = requestToDomain(client_id, redirect_uri, response_type, scope, state);
        var user = userToDomain(userDTO);

        return getAuthenticationTicket(request, user);
    }

    private Request requestToDomain(String client_id, String redirect_uri, String response_type, String scope, String state) {
        var requestDTO = RequestDTO.builder()
                .clientId(client_id)
                .redirectUri(redirect_uri)
                .responseType(response_type)
                .scope(scope)
                .state(state)
                .build();

        return requestDTO.toDomain();
    }

    private User userToDomain(UserDTO userDTO) {
        return userDTO.toHashedDomain();
    }

    private ResponseEntity<?> getAuthenticationTicket(Request request, User user) {
        try {
            var body = tokenService.authenticationTicket(user, request);
            var location = request.getRedirectUri();

            return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, location).body(body);
        } catch (ErrorResponseException e) {
            return new ResponseEntity<>(e.getErrorResponse(), HttpStatus.FOUND);
        }
    }
}
