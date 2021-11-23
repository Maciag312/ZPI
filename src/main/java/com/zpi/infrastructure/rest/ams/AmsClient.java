package com.zpi.infrastructure.rest.ams;

import com.zpi.api.common.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.Optional;

@FeignClient(name = "ams", url = "${ams.url}/api")
public interface AmsClient {
    @GetMapping("authserver/client/{id}/")
    Optional<ClientDTO> clientDetails(@PathVariable String id);

    @PostMapping("/users")
    ResponseEntity<?> registerUser(@Valid @RequestBody UserDTO user);

    @PostMapping("/users/authenticate")
    ResponseEntity<?> authenticate(UserDTO userDTO);

    @PostMapping("/users/otp/generate")
    ResponseEntity<?> generateOtp_FORWARD(@Valid @RequestBody OtpRequestDTO_FIXME request);
}