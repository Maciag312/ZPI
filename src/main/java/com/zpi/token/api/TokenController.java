package com.zpi.token.api;

import com.zpi.token.api.authorizationRequest.RequestDTO;
import com.zpi.token.domain.TokenService;
import com.zpi.utils.BasicAuth;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/token")
public class TokenController {
    private final TokenService tokenService;

    @PostMapping("/authorize")
    public ResponseEntity<?> authorize(@RequestHeader("Authorization") String credentials, @RequestBody RequestDTO requestDTO) {
        return tokenService.validateAuthorizationRequest(requestDTO, new BasicAuth(credentials));
    }
}
