package com.zpi.domain.authCode.consentRequest.authCodePersister;

import com.zpi.domain.authCode.consentRequest.AuthCode;
import com.zpi.domain.authCode.consentRequest.TicketData;

public interface AuthCodePersister {
    AuthCode persist(TicketData data);
}
