package com.zpi.infrastructure.authCode.consentRequest;

import com.zpi.domain.authCode.consentRequest.AuthCode;
import com.zpi.domain.authCode.consentRequest.authCodeIssuer.AuthCodeRepository;
import com.zpi.infrastructure.common.InMemoryEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryAuthCodeRepository extends InMemoryEntityRepository<AuthCode, AuthCodeTuple> implements AuthCodeRepository {
    @Override
    public void save(String key, AuthCode item) {
        super.getItems().put(key, new AuthCodeTuple(key, item));
    }
}
