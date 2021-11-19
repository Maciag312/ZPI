package com.zpi.infrastructure.rest.analysis;

import com.zpi.domain.rest.analysis.twoFactor.TwoFactorResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TwoFactorDTO {
    private boolean additionalLayerRequired;

    public TwoFactorResponse toDomain() {
        return new TwoFactorResponse(additionalLayerRequired);
    }
}
