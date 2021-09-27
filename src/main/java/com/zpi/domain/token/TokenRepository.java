package com.zpi.domain.token;

import com.zpi.domain.common.EntityRepository;
import com.zpi.domain.token.issuer.TokenData;

public interface TokenRepository extends EntityRepository<String, TokenData> {
    void remove(String key);
}
