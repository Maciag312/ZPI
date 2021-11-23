package com.zpi.infrastructure.rest.analysis;

import com.zpi.domain.rest.analysis.AnalysisResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AnalysisResponseDTO {
    private LockoutDTO loginFailed;
    private TwoFactorDTO twoFactor;

    public AnalysisResponse toDomain() {
        return new AnalysisResponse(loginFailed.toDomain(), twoFactor.toDomain());
    }
}
