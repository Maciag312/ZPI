package com.zpi.domain.rest.analysis;

import com.zpi.domain.rest.ams.User;
import com.zpi.domain.rest.analysis.lockout.Lockout;
import com.zpi.domain.rest.analysis.twoFactor.AnalysisRequest;
import com.zpi.infrastructure.rest.analysis.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AnalysisServiceImpl implements AnalysisService {
    private final AnalysisClient client;
    private final AnalysisServiceFallback fallback;

    @Override
    public AnalysisResponse analyse(AnalysisRequest request) {
        try {
            return client.analyse(new AnalysisRequestDTO(request)).toDomain();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return fallback.analyse(new AnalysisRequestDTO(request)).toDomain();
        }
    }

    @Override
    public void reportLoginFail(AnalysisRequest request) {
        try {
            client.reportLoginFail(new AnalysisRequestDTO(request));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            fallback.reportLoginFail(new AnalysisRequestDTO(request));
        }
    }

    @Override
    public Lockout lockoutInfo(User user) {
        try {
            return client.lockoutInfo(user.getEmail()).toDomain();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return fallback.lockoutInfo(user.getEmail()).toDomain();
        }
    }
}
