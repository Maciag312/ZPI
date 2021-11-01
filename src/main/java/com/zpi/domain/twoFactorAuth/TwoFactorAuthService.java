package com.zpi.domain.twoFactorAuth;

import com.zpi.api.common.exception.ErrorResponseException;

public interface TwoFactorAuthService {
    TwoFactorAuthResponse validate(TwoFactorData data) throws ErrorResponseException;
}
