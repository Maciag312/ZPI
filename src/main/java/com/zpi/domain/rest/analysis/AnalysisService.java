package com.zpi.domain.rest.analysis;

import com.zpi.domain.rest.analysis.afterLogin.AnalysisRequest;
import com.zpi.domain.rest.analysis.failedLogin.LockoutResponse;

public interface AnalysisService {
    boolean isAdditionalLayerRequired(AnalysisRequest request);

    LockoutResponse failedLoginLockout(AnalysisRequest request);
}
