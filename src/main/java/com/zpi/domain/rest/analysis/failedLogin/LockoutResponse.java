package com.zpi.domain.rest.analysis.failedLogin;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Getter
public class LockoutResponse {
    private final LoginAction action;
    private final LocalDateTime delayTill;
}
