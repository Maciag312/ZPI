package com.zpi.domain.rest.ams;

import com.zpi.api.common.dto.UserDTO;
import com.zpi.infrastructure.rest.ams.AmsClient;
import com.zpi.infrastructure.rest.ams.AmsClientFallback;
import com.zpi.infrastructure.rest.ams.ClientDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class AmsServiceImpl implements AmsService {
    private final AmsClient client;
    private final AmsClientFallback fallback;

    @Override
    public Optional<Client> clientDetails(String id) {
        try {
            return client.clientDetails(id).map(ClientDTO::toDomain);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return fallback.clientDetails(id).map(ClientDTO::toDomain);
        }
    }

    @Override
    public boolean isAuthenticated(User user) {
        try {
            return client.authenticate(new UserDTO(user.getEmail(), user.getPassword())).getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return fallback.authenticate(new UserDTO(user.getEmail(), user.getPassword())).getStatusCode() == HttpStatus.OK;
        }
    }

    @Override
    public AuthConfiguration config() {
        try {
            return client.tokenConfig().toDomain();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return fallback.tokenConfig().toDomain();
        }
    }
}
