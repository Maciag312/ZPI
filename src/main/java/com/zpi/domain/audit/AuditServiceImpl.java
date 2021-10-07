package com.zpi.domain.audit;

import com.zpi.api.common.dto.ErrorResponseDTO;
import com.zpi.api.common.exception.ErrorResponseException;
import com.zpi.domain.authCode.authenticationRequest.AuthenticationRequest;
import com.zpi.domain.common.RequestError;
import com.zpi.domain.organization.Organization;
import com.zpi.domain.organization.OrganizationRepository;
import com.zpi.domain.organization.client.Client;
import com.zpi.domain.organization.client.ClientRepository;
import com.zpi.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {
    private final ClientRepository clientRepository;
    private final AuditRepository auditRepository;
    private final OrganizationRepository organizationRepository;

    @Override
    public void audit(User user, AuthenticationRequest request, AuditMetadata metadata) {
        try {
            var data = constructAuditData(user, request, metadata);
            auditRepository.save(data.getDate(), data);
        } catch (Exception ignored) {
        }
    }

    @Override
    public List<AuditLog> findByOrganization(String organizationName) throws ErrorResponseException {
        if (organizationRepository.findByName(organizationName).isEmpty()) {
            var error = RequestError.<AuditRequestErrorType>builder()
                    .error(AuditRequestErrorType.UNKNOWN_ORGANIZATION)
                    .errorDescription("Unknown organization: " + organizationName)
                    .build();
            var errorResponse = new ErrorResponseDTO<>(error, "");

            throw new ErrorResponseException(errorResponse);
        }

        return auditRepository.findByOrganization(new Organization(organizationName));
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
