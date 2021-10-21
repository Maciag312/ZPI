package com.zpi.domain.audit;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class AuditLog {
    private final Date date;
    private final AuditMetadata metadata;
    private final String username;
}
