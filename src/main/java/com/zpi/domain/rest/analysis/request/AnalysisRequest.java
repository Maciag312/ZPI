package com.zpi.domain.rest.analysis.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AnalysisRequest {
    private final DeviceInfo deviceInfo;
    private final IpInfo ipInfo;
    private final AuditUser user;
}
