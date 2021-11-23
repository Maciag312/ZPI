package com.zpi.infrastructure.rest.analysis;

import com.zpi.domain.rest.analysis.twoFactor.TwoFactorResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
class TwoFactorDTO {
    private boolean additionalLayerRequired;

    TwoFactorResponse toDomain() {
        return new TwoFactorResponse(additionalLayerRequired);
    }
}
