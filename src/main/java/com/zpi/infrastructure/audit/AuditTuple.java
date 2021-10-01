package com.zpi.infrastructure.audit;

import com.zpi.domain.audit.AuditData;
import com.zpi.infrastructure.common.EntityTuple;
import lombok.Data;
import lombok.Getter;

import java.util.Date;

@Data
@Getter
class AuditTuple implements EntityTuple<AuditData> {
    private final Date date;
    private final String host;
    private final String userAgent;
    private final String organizationName;

    AuditTuple(AuditData data) {
        this.date = data.getDate();
        this.host = data.getHost();
        this.userAgent = data.getUserAgent();
        this.organizationName = data.getOrganizationName();
    }

    @Override
    public AuditData toDomain() {
        return new AuditData(date, host, userAgent, organizationName);
    }
}
