package com.zpi.infrastructure.audit;

import com.zpi.domain.audit.AuditLog;
import com.zpi.domain.audit.AuditMetadata;
import com.zpi.infrastructure.common.EntityTuple;
import lombok.Data;
import lombok.Getter;

import java.util.Date;

@Data
@Getter
class AuditLogTuple implements EntityTuple<AuditLog> {
    private final Date date;
    private final String host;
    private final String userAgent;
    private final String username;

    AuditLogTuple(AuditLog data) {
        this.date = data.getDate();
        this.host = data.getMetadata().getHost();
        this.userAgent = data.getMetadata().getUserAgent();
        this.username = data.getUsername();
    }

    @Override
    public AuditLog toDomain() {
        var metadata = new AuditMetadata(host, userAgent);
        return new AuditLog(date, metadata, username);
    }
}
