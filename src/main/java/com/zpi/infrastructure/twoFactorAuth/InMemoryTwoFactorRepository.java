package com.zpi.infrastructure.twoFactorAuth;

import com.zpi.domain.twoFactorAuth.TwoFactorData;
import com.zpi.domain.twoFactorAuth.TwoFactorRepository;
import com.zpi.infrastructure.common.EntityTuple;
import com.zpi.infrastructure.common.InMemoryRepository;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryTwoFactorRepository extends InMemoryRepository<String, TwoFactorData> implements TwoFactorRepository {
    @Override
    public EntityTuple<TwoFactorData> fromDomain(TwoFactorData entity) {
        return new TwoFactorTuple(entity.getTicket(), entity.getTwoFactorCode());
    }

    @Override
    public void remove(String key) {
        super.repository.remove(key);
    }
}
