package com.zpi.api.audit;

import com.zpi.api.common.exception.ErrorResponseException;
import com.zpi.domain.audit.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuditController {
    public final static String auditUri = "/audit";

    private final AuditService service;

    @GetMapping(auditUri + "/{organizationName}")
    public ResponseEntity<?> audit(@PathVariable String organizationName) {
        try {
            var result = service.findByOrganization(organizationName);
            return ResponseEntity.ok(result);
        } catch (ErrorResponseException e) {
            return new ResponseEntity<>(e.getErrorResponse(), HttpStatus.NOT_FOUND);
        }
    }
}
