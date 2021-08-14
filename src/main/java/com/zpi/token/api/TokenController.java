package com.zpi.token.api;

import com.zpi.token.api.authorizationRequest.RequestDTO;
import com.zpi.token.domain.TokenService;
import com.zpi.common.api.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/token")
public class TokenController {
    private final TokenService tokenService;

    @PostMapping("/authorize")
    public ResponseEntity<?> authorize(@RequestBody UserDTO userDto, @RequestParam Map<String, String> params) {
        var requestDto = RequestDTO.builder()
                .clientId(params.get("client_id"))
                .redirectUri(params.get("redirect_uri"))
                .responseType(params.get("response_type"))
                .scope(params.get("scope"))
                .state(params.get("state"))
                .build();

        return tokenService.authorizationRequest(userDto, requestDto);
    }
}
