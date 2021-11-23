package com.zpi.infrastructure.rest.analysis;

import com.zpi.domain.rest.analysis.lockout.LoginAction;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AnalysisServiceFallbackImpl implements AnalysisServiceFallback {
    @Override
    public AnalysisResponseDTO analyse(AnalysisRequestDTO request) {
        return null;
    }

    @Override
    public void reportLoginFail(AnalysisRequestDTO request) {
    }

    @Override
    public LockoutDTO lockoutInfo(String username) {
        return new LockoutDTO(LoginAction.BLOCK.toString(), LocalDateTime.now().toString());
    }
}
