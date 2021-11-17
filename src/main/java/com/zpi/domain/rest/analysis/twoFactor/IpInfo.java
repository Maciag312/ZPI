package com.zpi.domain.rest.analysis.twoFactor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class IpInfo {
    private final String city;
    private final String continentCode;
    private final String continentName;
    private final String countryCode;
    private final String countryName;
    private final String ipAddress;
    private final String stateProv;
}
