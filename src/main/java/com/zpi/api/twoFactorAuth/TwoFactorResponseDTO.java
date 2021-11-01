package com.zpi.api.twoFactorAuth;

import com.zpi.domain.twoFactorAuth.TwoFactorAuthResponse;
import lombok.Getter;

@Getter
class TwoFactorResponseDTO {
    private final String ticket;

    TwoFactorResponseDTO(TwoFactorAuthResponse response) {
        this.ticket = response.getTicket();
    }
}
