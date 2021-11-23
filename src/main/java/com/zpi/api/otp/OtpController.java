package com.zpi.api.otp;

import com.zpi.infrastructure.rest.ams.AmsClient;
import com.zpi.infrastructure.rest.ams.OtpRequestDTO_FIXME;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.ResponseEntity.*;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/otp")
public class OtpController {
    private final AmsClient client;

    @PostMapping("/generate")
    public ResponseEntity<?> generate(@Valid @RequestBody OtpRequestDTO_FIXME request) {
        try {
            client.generateOtp_FORWARD(request);
            return ok().build();
        } catch (IllegalArgumentException ex) {
            return status(UNAUTHORIZED).body(ex);
        } catch (Exception e) {
            return internalServerError().body(e);
        }
    }
}
