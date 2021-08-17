package com.zpi.api.token.consentRequest;

import com.zpi.domain.token.consentRequest.ConsentRequest;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ConsentRequestDTO {
    private final String ticket;
    private final String state;

    public ConsentRequest toDomain() {
        return ConsentRequest.builder()
                .ticket(ticket)
                .state(state)
                .build();
    }
}
