package com.zpi.infrastructure.rest.analysis;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AuditUserDTO {
    private final String login;
}
