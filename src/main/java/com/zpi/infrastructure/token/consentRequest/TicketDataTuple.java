package com.zpi.infrastructure.token.consentRequest;

import com.zpi.domain.token.consentRequest.TicketData;
import com.zpi.infrastructure.common.EntityTuple;
import lombok.Getter;

import javax.persistence.Id;

@Getter
class TicketDataTuple implements EntityTuple<TicketData> {
    @Id
    private final String id;

    TicketDataTuple(String ticket) {
        this.id = ticket;
    }

    public TicketData toDomain() {
        return TicketData.builder().build();
    }
}
