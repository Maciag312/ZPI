package com.zpi.domain.rest.analysis;

import com.zpi.domain.rest.analysis.request.AnalysisRequest;

public interface AnalysisService {
    boolean isAdditionalLayerRequired(AnalysisRequest request);
    void reportFailedLogin(AnalysisRequest request);
}
