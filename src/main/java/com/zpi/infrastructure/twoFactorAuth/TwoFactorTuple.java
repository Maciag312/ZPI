package com.zpi.infrastructure.twoFactorAuth;

import com.zpi.domain.twoFactorAuth.TwoFactorData;
import com.zpi.infrastructure.common.EntityTuple;
import lombok.Data;
import lombok.Getter;

@Getter
@Data
class TwoFactorTuple implements EntityTuple<TwoFactorData> {
    private final String ticket;
    private final String twoFactorCode;

    @Override
    public TwoFactorData toDomain() {
        return new TwoFactorData(ticket, twoFactorCode);
    }
}
