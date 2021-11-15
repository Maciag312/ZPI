package com.zpi.domain.rest.analysis.afterLogin;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AuditUser {
    private final String login;
}
