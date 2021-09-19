package com.zpi.api.authCode.consentRequest;

import com.zpi.domain.authCode.consentRequest.ConsentRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ConsentRequestDTO {
    private String ticket;
    private String state;

    public ConsentRequest toDomain() {
        return ConsentRequest.builder()
                .ticket(ticket)
                .state(state)
                .build();
    }
}
