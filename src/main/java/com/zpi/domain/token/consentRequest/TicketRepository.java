package com.zpi.domain.token.consentRequest;

import com.zpi.domain.common.EntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends EntityRepository<TicketData> {
    void remove(String key);
}
