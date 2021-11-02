package com.zpi.domain.twoFactorAuth;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TwoFactorAuthResponse {
    private final String ticket;
}
