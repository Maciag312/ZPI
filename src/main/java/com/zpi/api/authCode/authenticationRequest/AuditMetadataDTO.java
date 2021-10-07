package com.zpi.api.authCode.authenticationRequest;

import com.zpi.domain.audit.AuditMetadata;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuditMetadataDTO {
    private final String host;
    private final String userAgent;

    public AuditMetadata toDomain() {
        return new AuditMetadata(host, userAgent);
    }
}
