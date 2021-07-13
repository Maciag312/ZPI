package com.zpi.user;

import com.zpi.user.api.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public ResponseEntity<?> createUser(UserDTO userDTO) {
        User user = userDTO.toHashedDomain();
        var login = user.getLogin();

        if (userRepository.getByKey(login).isPresent()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        userRepository.save(login, user);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
