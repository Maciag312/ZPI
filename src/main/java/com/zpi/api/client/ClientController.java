package com.zpi.api.client;

import com.zpi.domain.client.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/client")
public class ClientController {
    private final ClientService service;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody ClientDTO client) {
        var domain = client.toDomain();

        if (service.saveClient(domain))
            return new ResponseEntity<>(HttpStatus.CREATED);

        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }
}
