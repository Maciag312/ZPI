package com.zpi.infrastructure.authCode.consentRequest;

import com.zpi.domain.authCode.consentRequest.TicketData;
import com.zpi.domain.authCode.consentRequest.TicketRepository;
import com.zpi.infrastructure.common.EntityTuple;
import com.zpi.infrastructure.common.InMemoryRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class InMemoryTicketRepository extends InMemoryRepository<String, TicketData> implements TicketRepository {


    @Override
    public EntityTuple<TicketData> fromDomain(TicketData entity) {
        return new TicketDataTuple(entity);
    }

    @Override
    public void remove(String key) {
        repository.remove(key);
    }
}
