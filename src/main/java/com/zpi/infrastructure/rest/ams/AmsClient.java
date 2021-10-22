package com.zpi.infrastructure.rest.ams;

import com.zpi.api.common.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.Optional;

@FeignClient(name = "ams", url = "${ams.url}/api/authserver")
public interface AmsClient {
    @GetMapping("/client/{id}/")
    Optional<ClientDTO> clientDetails(@PathVariable String id);

    @PostMapping("/user/register")
    boolean registerUser(@Valid @RequestBody UserDTO userDTO);

    @GetMapping("/user/authenticate")
    boolean isAuthenticated(UserDTO userDTO);

    @Component
    class AmsClientFallback implements AmsClient {
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
            return user.getLogin().equals("s") && user.getPassword().equals("s");
        }
    }
}