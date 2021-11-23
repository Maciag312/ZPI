package com.zpi.domain.rest.analysis;

import com.zpi.domain.rest.analysis.lockout.Lockout;
import com.zpi.domain.rest.analysis.twoFactor.TwoFactorResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AnalysisResponse {
    private final Lockout lockout;
    private final TwoFactorResponse twoFactor;
}
