package com.zpi.infrastructure.rest.analysis;

import org.springframework.stereotype.Component;

@Component
public class AnalysisServiceFallbackImpl implements AnalysisServiceFallback {
    @Override
    public boolean isAdditionalLayerRequired(AnalysisRequestDTO request) {
        return true;
    }
}
