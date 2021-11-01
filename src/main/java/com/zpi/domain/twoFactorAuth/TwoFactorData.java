package com.zpi.domain.twoFactorAuth;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class TwoFactorData {
    private final String ticket;
    private final String twoFactorCode;
}
