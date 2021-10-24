package com.zpi.domain.rest.analysis;

import com.zpi.infrastructure.rest.analysis.AnalysisRequestDTO;
import org.springframework.stereotype.Component;

@Component
public class AnalysisServiceFallbackImpl implements AnalysisServiceFallback {
    @Override
    public boolean isAdditionalLayerRequired(AnalysisRequestDTO request) {
        return true;
    }
}
