package com.zpi.api.organization.client;

import com.zpi.domain.organization.OrganizationService;
import com.zpi.domain.organization.client.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/organization/{name}/client")
public class ClientController {
    private final ClientService service;
    private final OrganizationService organizationService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody ClientDTO client, @PathVariable String name) {
        if(!organizationService.exists(name)){
            throw new IllegalArgumentException("Organization with such name doesn't exists");
        }

        var domain = client.toDomain();
        if (service.saveClient(domain))
            return new ResponseEntity<>(HttpStatus.CREATED);

        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }
}
