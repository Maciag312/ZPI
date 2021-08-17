package com.zpi.infrastructure.token.consentRequest;

import com.zpi.domain.token.consentRequest.TicketData;
import com.zpi.domain.token.consentRequest.TicketRepository;
import com.zpi.infrastructure.common.InMemoryEntityRepository;
import org.springframework.stereotype.Component;

@Component
public class InMemoryTicketRepository extends InMemoryEntityRepository<TicketData, TicketDataTuple> implements TicketRepository {
    @Override
    public void save(String key, TicketData data) {
        super.getItems().put(key, new TicketDataTuple(key));
    }

    @Override
    public void remove(String ticket) {
        super.getItems().remove(ticket);
    }
}
