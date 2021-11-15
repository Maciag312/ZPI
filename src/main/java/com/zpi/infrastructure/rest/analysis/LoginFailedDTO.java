package com.zpi.infrastructure.rest.analysis;

import com.zpi.domain.rest.analysis.failedLogin.LockoutResponse;
import com.zpi.domain.rest.analysis.failedLogin.LoginAction;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class LoginFailedDTO {
    private final String loginAction;
    private final String delayTill;

    public LockoutResponse toDomain() {
        var action = this.loginAction.equals("ALLOW") ? LoginAction.ALLOW : LoginAction.BLOCK;
        var delay = LocalDateTime.parse(this.delayTill);
        return new LockoutResponse(action, delay);
    }
}
