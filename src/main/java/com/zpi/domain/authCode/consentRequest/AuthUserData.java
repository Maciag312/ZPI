package com.zpi.domain.authCode.consentRequest;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class AuthUserData {
    private final String scope;
    private final String redirectUri;
    private final String username;
}
