package com.zpi.domain.audit;

import com.zpi.domain.authCode.authenticationRequest.AuthenticationRequest;
import com.zpi.domain.rest.ams.User;

import java.util.List;

public interface AuditService {
    void audit(User user, AuthenticationRequest request, AuditMetadata metadata);
    List<AuditLog> findByUsername(String username);
}
