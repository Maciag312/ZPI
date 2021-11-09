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
        var domain = new UserDTO("user@zpi.com", "s").toDomain();
        if (user == null || user.getEmail() == null || user.getPassword() == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (user.getEmail().equals(domain.getEmail()) && user.getPassword().equals(domain.getPassword())) {
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
}
