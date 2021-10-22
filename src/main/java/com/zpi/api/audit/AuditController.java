package com.zpi.api.audit;

import com.zpi.domain.audit.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/audit")
public class AuditController {
    private final AuditService service;

    @GetMapping("/users/{username}")
    public ResponseEntity<?> audit(@PathVariable String username) {
        return ResponseEntity.ok(service.findByUsername(username));
    }
}
