package com.zpi.domain.authCode.consentRequest;

public interface ConsentService {
    ConsentResponse consent(ConsentRequest request) throws ErrorConsentResponseException;
}
