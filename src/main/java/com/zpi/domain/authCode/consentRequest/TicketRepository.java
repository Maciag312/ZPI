package com.zpi.domain.authCode.consentRequest;

import com.zpi.domain.common.EntityRepository;
import org.springframework.stereotype.Repository;

public interface TicketRepository extends EntityRepository<TicketData> {
    void remove(String key);
}
