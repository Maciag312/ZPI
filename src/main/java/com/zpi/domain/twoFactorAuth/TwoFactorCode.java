package com.zpi.domain.twoFactorAuth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
class TwoFactorCode {
    private final String code;
}
