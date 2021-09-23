package com.zpi.domain.token.refreshRequest;

import com.zpi.domain.common.EntityRepository;

public interface TokenRepository extends EntityRepository<String, TokenData> {
    void remove(String key);
}
