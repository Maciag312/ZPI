package com.zpi.domain.audit;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuditRequestErrorType {
    UNKNOWN_ORGANIZATION("UNKNOWN_ORGANIZATION");

    private final String name;
}
