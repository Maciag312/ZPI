package com.zpi.domain.audit;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AuditMetadata {
    private final String host;
    private final String userAgent;
}
