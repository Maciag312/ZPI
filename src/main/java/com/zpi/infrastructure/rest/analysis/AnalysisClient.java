package com.zpi.infrastructure.rest.analysis;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@FeignClient(name = "analysis", url = "${analysis.url}/api/authserver")
public interface AnalysisClient {
    @PostMapping("/analyse")
    AnalysisResponseDTO analyse(@Valid @RequestBody AnalysisRequestDTO request);

    @PostMapping("/login-fail")
    void reportLoginFail(@Valid @RequestBody AnalysisRequestDTO request);
}
