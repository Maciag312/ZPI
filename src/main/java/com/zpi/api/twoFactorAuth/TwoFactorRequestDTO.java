package com.zpi.api.twoFactorAuth;

import com.zpi.domain.twoFactorAuth.TwoFactorData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
@NoArgsConstructor
class TwoFactorRequestDTO {
    @NotNull
    private String ticket;

    @NotNull
    private String code;

    TwoFactorData toDomain() {
        return new TwoFactorData(ticket, code);
    }
}
