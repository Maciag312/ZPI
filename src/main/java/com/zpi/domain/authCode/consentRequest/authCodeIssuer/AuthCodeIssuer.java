package com.zpi.domain.authCode.consentRequest.authCodeIssuer;

import com.zpi.domain.authCode.consentRequest.AuthCode;

public interface AuthCodeIssuer {
    AuthCode issue();
}
