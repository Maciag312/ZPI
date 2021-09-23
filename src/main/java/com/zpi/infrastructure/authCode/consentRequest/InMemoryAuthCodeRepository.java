package com.zpi.infrastructure.authCode.consentRequest;

import com.zpi.domain.authCode.consentRequest.AuthCode;
import com.zpi.domain.authCode.consentRequest.authCodePersister.AuthCodeRepository;
import com.zpi.infrastructure.common.EntityTuple;
import com.zpi.infrastructure.common.InMemoryRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class InMemoryAuthCodeRepository extends InMemoryRepository<String, AuthCode> implements AuthCodeRepository {
    @Override
    public EntityTuple<AuthCode> fromDomain(AuthCode code) {
        return new AuthCodeTuple(code.getValue(), code);
    }

    @Override
    public void remove(String key) {
        super.repository.remove(key);
    }
}
