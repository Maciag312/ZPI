package com.zpi.infrastructure.authCode.consentRequest;

import com.zpi.domain.authCode.consentRequest.TicketData;
import com.zpi.domain.authCode.consentRequest.TicketRepository;
import com.zpi.infrastructure.common.InMemoryEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryTicketRepository extends InMemoryEntityRepository<TicketData, TicketDataTuple> implements TicketRepository {
    @Override
    public void save(String key, TicketData data) {
        super.getItems().put(key, new TicketDataTuple(key, data));
    }

    @Override
    public void remove(String ticket) {
        super.getItems().remove(ticket);
    }
}
