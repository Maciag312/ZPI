package com.zpi.infrastructure.rest.ams;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@FeignClient(name = "ams", url = "${ams.url}/api/authserver")
public interface AmsClient {
    @GetMapping("/client/{id}/")
    Optional<ClientDTO> clientDetails(@PathVariable String id);
}