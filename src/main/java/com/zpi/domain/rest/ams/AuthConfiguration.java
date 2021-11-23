package com.zpi.domain.rest.ams;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AuthConfiguration {
    private final String secretKey;
    private final long expirationTime;
}
