package com.zpi.infrastructure.rest.analysis;

import org.springframework.stereotype.Component;

@Component
public class AnalysisServiceFallbackImpl implements AnalysisServiceFallback {
    @Override
    public AnalysisResponseDTO analyse(AnalysisRequestDTO request) {
        return null;
    }

    @Override
    public void reportLoginFail(AnalysisRequestDTO request) {
    }
}
