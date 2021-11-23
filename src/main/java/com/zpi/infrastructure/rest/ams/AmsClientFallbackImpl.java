package com.zpi.infrastructure.rest.ams;

import com.zpi.api.common.dto.UserDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AmsClientFallbackImpl implements AmsClientFallback {
    @Override
    public Optional<ClientDTO> clientDetails(String id) {
        return Optional.empty();
    }

    @Override
    public ResponseEntity<?> registerUser(UserDTO user) {
        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }

    @Override
    public ResponseEntity<?> authenticate(UserDTO user) {
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @Override
    public ResponseEntity<?> generateOtp_FORWARD(OtpRequestDTO_FIXME request) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("AMS not available");
    }
}
