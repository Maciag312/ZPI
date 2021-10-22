package com.zpi.domain.audit;

import com.zpi.domain.authCode.authenticationRequest.AuthenticationRequest;
import com.zpi.domain.rest.ams.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {
    private final AuditRepository auditRepository;

    @Override
    public void audit(User user, AuthenticationRequest request, AuditMetadata metadata) {
        var data = constructAuditData(user, request, metadata);
        auditRepository.save(data.getDate(), data);
    }

    @Override
    public List<AuditLog> findByUsername(String username) {
        return auditRepository.findByUsername(username);
    }

    private AuditLog constructAuditData(User user, AuthenticationRequest request, AuditMetadata metadata) {
        var username = user.getLogin();
        var date = new Date();

        return new AuditLog(date, metadata, username);
    }
}
