package com.zpi.infrastructure.rest.analysis;

import com.zpi.domain.rest.analysis.lockout.LoginFailedResponse;
import com.zpi.domain.rest.analysis.lockout.LoginAction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoginFailedDTO {
    private String loginAction;
    private String delayTill;

    public LoginFailedResponse toDomain() {
        var action = this.loginAction.equals("ALLOW") ? LoginAction.ALLOW : LoginAction.BLOCK;
        var delay = LocalDateTime.parse(this.delayTill);
        return new LoginFailedResponse(action, delay);
    }
}
