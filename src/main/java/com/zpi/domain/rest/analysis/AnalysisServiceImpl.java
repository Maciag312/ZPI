package com.zpi.domain.rest.analysis;

import com.zpi.domain.rest.ams.User;
import com.zpi.domain.rest.analysis.lockout.Lockout;
import com.zpi.domain.rest.analysis.twoFactor.AnalysisRequest;
import com.zpi.infrastructure.rest.analysis.AnalysisClient;
import com.zpi.infrastructure.rest.analysis.AnalysisRequestDTO;
import com.zpi.infrastructure.rest.analysis.AnalysisServiceFallback;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AnalysisServiceImpl implements AnalysisService {
    private final AnalysisClient client;
    private final AnalysisServiceFallback fallback;

    Logger logger = LoggerFactory.getLogger(AnalysisServiceImpl.class);

    @Override
    public AnalysisResponse analyse(AnalysisRequest request) {
        try {
            return client.analyse(new AnalysisRequestDTO(request)).toDomain();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return fallback.analyse(new AnalysisRequestDTO(request)).toDomain();
        }
    }

    @Override
    public void reportLoginFail(AnalysisRequest request) {
        try {
            client.reportLoginFail(new AnalysisRequestDTO(request));
        } catch (Exception e) {
            logger.error(e.getMessage());
            fallback.reportLoginFail(new AnalysisRequestDTO(request));
        }
    }

    @Override
    public Lockout lockoutInfo(User user) {
        try {
            return client.lockoutInfo(user.getEmail()).toDomain();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return fallback.lockoutInfo(user.getEmail()).toDomain();
        }
    }
}
