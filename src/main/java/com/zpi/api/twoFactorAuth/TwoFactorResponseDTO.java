package com.zpi.api.twoFactorAuth;

import com.zpi.domain.twoFactorAuth.TwoFactorAuthResponse;
import lombok.Getter;

@Getter
class TwoFactorResponseDTO {
    private final String ticket;
    private final String scope;

    TwoFactorResponseDTO(TwoFactorAuthResponse response) {
        this.ticket = response.getTicket();
//FIXME hardcoded scope
        this.scope = "profile";
    }
}
