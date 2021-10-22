package com.zpi.domain.rest.ams;

import com.zpi.api.common.dto.UserDTO;
import com.zpi.infrastructure.rest.ams.ClientDTO;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AmsServiceFallbackImpl implements AmsServiceFallback {
    @Override
    public Optional<ClientDTO> clientDetails(String id) {
        return Optional.empty();
    }

    @Override
    public boolean registerUser(UserDTO user) {
        return !isAuthenticated(user);
    }

    @Override
    public boolean isAuthenticated(UserDTO user) {
        var hashed = new UserDTO("s", "s").toHashedDomain();
        return user.getLogin().equals(hashed.getLogin()) && user.getPassword().equals(hashed.getPassword());
    }
}
