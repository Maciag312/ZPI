package com.zpi.domain.authCode.consentRequest;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ConsentResponse {
    private final AuthCode code;
    private final String state;
    private final String redirectUri;
}
