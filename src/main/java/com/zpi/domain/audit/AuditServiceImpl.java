package com.zpi.domain.audit;

import com.zpi.domain.authCode.authenticationRequest.AuthenticationRequest;
import com.zpi.domain.organization.client.Client;
import com.zpi.domain.organization.client.ClientRepository;
import com.zpi.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {
    private final ClientRepository clientRepository;
    private final AuditRepository auditRepository;

    @Override
    public void audit(User user, AuthenticationRequest request, AuditMetadata metadata) {
        try {
            var data = constructAuditData(user, request, metadata);
            auditRepository.save(data.getDate(), data);
        } catch (Exception ignored) {
        }
    }

    private AuditLog constructAuditData(User user, AuthenticationRequest request, AuditMetadata metadata) throws Exception {
        var organizationName = findOrganizationName(request);
        var username = user.getLogin();

        var date = new Date();
        return new AuditLog(date, metadata, organizationName, username);
    }

    private String findOrganizationName(AuthenticationRequest request) throws Exception {
        var clientId = request.getClientId();

        if (clientId == null)
            throw new Exception();

        var client = clientRepository.findByKey(clientId);
        return client.map(Client::getOrganizationName).orElseThrow();
    }
}
