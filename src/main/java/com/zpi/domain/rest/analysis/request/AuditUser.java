package com.zpi.domain.rest.analysis.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AuditUser {
    private final String login;
}
