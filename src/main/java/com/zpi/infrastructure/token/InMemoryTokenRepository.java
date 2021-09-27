package com.zpi.infrastructure.token;

import com.zpi.domain.token.issuer.TokenData;
import com.zpi.domain.token.TokenRepository;
import com.zpi.infrastructure.common.EntityTuple;
import com.zpi.infrastructure.common.InMemoryRepository;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryTokenRepository extends InMemoryRepository<String, TokenData> implements TokenRepository {
    @Override
    public EntityTuple<TokenData> fromDomain(TokenData data) {
        return new TokenTuple(data);
    }

    @Override
    public void remove(String key) {
        super.repository.remove(key);
    }
}
