package com.zpi.infrastructure.authCode.consentRequest;

import com.zpi.domain.authCode.consentRequest.TicketData;
import com.zpi.infrastructure.common.EntityTuple;
import lombok.Getter;

import javax.persistence.Id;

@Getter
class TicketDataTuple implements EntityTuple<TicketData> {
    @Id
    private final String id;

    private final String redirectUri;

    TicketDataTuple(String key, TicketData data) {
        this.id = key;
        this.redirectUri = data.getRedirectUri();
    }

    public TicketData toDomain() {
        return TicketData.builder()
                .redirectUri(redirectUri)
                .build();
    }
}
