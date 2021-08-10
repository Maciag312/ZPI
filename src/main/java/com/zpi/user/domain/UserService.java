package com.zpi.user.domain;

import com.zpi.common.api.UserDTO;
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

    public boolean isAuthenticated(UserDTO userDTO) {
        User user = userDTO.toHashedDomain();
        var login = user.getLogin();

        var found = userRepository.getByKey(login);

        if (found.isEmpty()) {
            return false;
        }

        return found.get().getPassword().equals(user.getPassword());
    }
}
