package com.zpi.api.twoFactorAuth;

import com.zpi.api.common.exception.ErrorResponseException;
import com.zpi.domain.twoFactorAuth.TwoFactorAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/2fa")
public class TwoFactorAuthController {
    private final TwoFactorAuthService service;

    @PostMapping
    public ResponseEntity<?> validate(@Valid @RequestBody TwoFactorRequestDTO request) {
        try {
            var response = new TwoFactorResponseDTO(service.validate(request.toDomain()));
            return ResponseEntity.ok(response);
        } catch (ErrorResponseException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e.getErrorResponse());
        }
    }
}
