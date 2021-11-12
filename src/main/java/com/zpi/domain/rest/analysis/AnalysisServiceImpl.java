package com.zpi.domain.rest.analysis;

import com.zpi.domain.rest.analysis.request.AnalysisRequest;
import com.zpi.infrastructure.rest.analysis.AnalysisClient;
import com.zpi.infrastructure.rest.analysis.AnalysisRequestDTO;
import com.zpi.infrastructure.rest.analysis.AnalysisServiceFallback;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AnalysisServiceImpl implements AnalysisService {
    private final AnalysisClient client;
    private final AnalysisServiceFallback fallback;

    @Override
    public boolean isAdditionalLayerRequired(AnalysisRequest request) {
        try {
            return client.isAdditionalLayerRequired(new AnalysisRequestDTO(request));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return fallback.isAdditionalLayerRequired(new AnalysisRequestDTO(request));
        }
    }

    @Override
    public void reportFailedLogin(AnalysisRequest request) {
        try {
            client.reportLoginFail(new AnalysisRequestDTO(request));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
