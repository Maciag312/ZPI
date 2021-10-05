package com.zpi.domain.audit;

import com.zpi.api.common.exception.ErrorResponseException;
import com.zpi.domain.authCode.authenticationRequest.AuthenticationRequest;
import com.zpi.domain.user.User;

import java.util.List;

public interface AuditService {
    void audit(User user, AuthenticationRequest request, AuditMetadata metadata);
    List<AuditLog> findByOrganization(String organizationName) throws ErrorResponseException;
}
