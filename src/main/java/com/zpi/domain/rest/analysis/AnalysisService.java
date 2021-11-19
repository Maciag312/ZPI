package com.zpi.domain.rest.analysis;

import com.zpi.domain.rest.analysis.twoFactor.AnalysisRequest;

public interface AnalysisService {
    AnalysisResponse analyse(AnalysisRequest request);

    void reportLoginFail(AnalysisRequest request);
}
