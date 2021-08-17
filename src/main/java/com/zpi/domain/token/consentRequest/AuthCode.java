package com.zpi.domain.token.consentRequest;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AuthCode {
    private final String value;
}
