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
    private final String scope;
    private final String username;

    TicketDataTuple(TicketData data) {
        this.redirectUri = data.getRedirectUri();
        this.scope = data.getScope();
        this.username = data.getUsername();
    }

    public TicketData toDomain() {
        return new TicketData(redirectUri, scope, username);
    }
}
