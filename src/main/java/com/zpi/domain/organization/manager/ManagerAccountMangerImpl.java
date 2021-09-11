package com.zpi.domain.organization.manager;

import com.zpi.infrastructure.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ManagerAccountMangerImpl implements ManagerAccountManager {

    private final ManagerRepository repository;
    private final JwtTokenProvider tokenProvider;

    @Override
    public String signIn(String organizationName, String username, String password) throws IllegalArgumentException {
        IllegalArgumentException exception =  new IllegalArgumentException("Manager is not found or provided credentials are wrong");
        var manager = repository.findByUsername(username)
                .orElseThrow(() -> exception);
        if (!manager.getPassword().equals(password)) {
            throw exception;
        }

        return tokenProvider.createToken(manager.getUsername(), manager.getRoles());
    }
}
