package com.zpi.domain.twoFactorAuth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TwoFactorErrorType {
    INCORRECT_2FA_CODE("INCORRECT_2FA_CODE");

    private final String name;
}
