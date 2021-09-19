package com.zpi.api.token;

import com.zpi.domain.token.TokenErrorResponseException;
import com.zpi.domain.token.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/token")
public class TokenController {
    private final TokenService service;

    @PostMapping
    public ResponseEntity<?> tokenRequest(@RequestBody TokenRequestDTO request) {
        try {
            var token = service.getToken(request.toDomain());
            return ResponseEntity.ok(TokenResponseDTO.builder().access_token(token.getValue()).build());
        } catch(TokenErrorResponseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new TokenErrorResponseDTO(e.getResponse()));
        }
    }
}
