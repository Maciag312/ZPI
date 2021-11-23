package com.zpi.infrastructure.rest.ams;

import com.zpi.api.common.dto.UserDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class AmsClientFallbackImpl implements AmsClientFallback {
    @Override
    public Optional<ClientDTO> clientDetails(String id) {
        return Optional.empty();
    }

    @Override
    public ResponseEntity<?> authenticate(UserDTO user) {
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @Override
    public ResponseEntity<?> generateOtp_FORWARD(OtpRequestDTO_FIXME request) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("AMS not available");
    }

    @Value("${jwt-default.expirationTime}")
    private int expirationTime;

    @Value("${jwt-default.secretKey}")
    private String secretKey;

    @Override
    public AuthConfigurationDTO tokenConfig() {
        var token = new TokenConfigurationDTO(expirationTime, secretKey);
        return new AuthConfigurationDTO(token);
    }

    @Override
    public UserInfoDTO userInfo(UserInfoRequestDTO user) {
        return new UserInfoDTO("", List.of(new PermissionDTO("", false)), List.of(""));
    }
}
