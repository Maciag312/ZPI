package com.zpi.domain.rest.analysis.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AnalysisResponse {
    private final boolean isAdditionalLayerRequired;
}
