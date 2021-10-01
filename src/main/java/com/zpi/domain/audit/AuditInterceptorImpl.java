package com.zpi.domain.audit;

import com.zpi.domain.organization.client.Client;
import com.zpi.domain.organization.client.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class AuditInterceptorImpl implements AuditInterceptor {
    private final AuditRepository auditRepository;
    private final ClientRepository clientRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        var date = new Date();
        var host = request.getHeader("host");
        var userAgent = request.getHeader("user-agent");
        var organizationName = findOrganizationName(request);

        var data = new AuditData(date, host, userAgent, organizationName);
        auditRepository.save(date, data);

        return true;
    }

    private String findOrganizationName(HttpServletRequest request) {
        var uri = request.getRequestURI() + "?" + request.getQueryString();
        var parsedUriParams = UriComponentsBuilder.fromUriString(uri).build().getQueryParams();

        var clientId = parsedUriParams.get("client_id");

        if (clientId == null) {
            return null;
        } else {
            var client = clientRepository.findByKey(clientId.get(0));

            return client.map(Client::getOrganizationName).orElse(null);
        }
    }
}
