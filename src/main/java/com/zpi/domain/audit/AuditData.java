package com.zpi.domain.audit;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class AuditData {
    private final Date date;
    private final String host;
    private final String userAgent;
    private final String organizationName;
}
