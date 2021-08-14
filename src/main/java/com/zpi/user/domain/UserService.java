package com.zpi.user.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public boolean createUser(User user) {
        var login = user.getLogin();

        if (userRepository.getByKey(login).isPresent()) {
            return false;
        }

        userRepository.save(login, user);
        return true;
    }

    public boolean isAuthenticated(User user) {
        var login = user.getLogin();

        var found = userRepository.getByKey(login);

        if (found.isEmpty()) {
            return false;
        }

        return found.get().getPassword().equals(user.getPassword());
    }
}
