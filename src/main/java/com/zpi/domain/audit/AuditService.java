package com.zpi.domain.audit;

import com.zpi.domain.authCode.authenticationRequest.AuthenticationRequest;
import com.zpi.domain.user.User;

public interface AuditService {
    void audit(User user, AuthenticationRequest request, AuditMetadata metadata);
}
