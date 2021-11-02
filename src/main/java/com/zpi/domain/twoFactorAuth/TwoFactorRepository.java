package com.zpi.domain.twoFactorAuth;

import com.zpi.domain.common.EntityRepository;

public interface TwoFactorRepository extends EntityRepository<String, TwoFactorData> {
    void remove(String key);
}
