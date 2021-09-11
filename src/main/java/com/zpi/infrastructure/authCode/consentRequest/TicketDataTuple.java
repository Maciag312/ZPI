package com.zpi.infrastructure.authCode.consentRequest;

import com.zpi.domain.authCode.consentRequest.TicketData;
import com.zpi.infrastructure.common.EntityTuple;
import lombok.Data;
import lombok.Getter;

import javax.persistence.Id;

@Getter
@Data
class TicketDataTuple implements EntityTuple<TicketData> {

    private final String redirectUri;

    TicketDataTuple(TicketData data) {
        this.redirectUri = data.getRedirectUri();
    }

    public TicketData toDomain() {
        return TicketData.builder()
                .redirectUri(redirectUri)
                .build();
    }
}
