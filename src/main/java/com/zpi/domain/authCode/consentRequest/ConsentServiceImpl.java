package com.zpi.domain.authCode.consentRequest;

import com.zpi.domain.authCode.consentRequest.authCodePersister.AuthCodePersister;
import com.zpi.domain.common.RequestError;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsentServiceImpl implements ConsentService {
    private final TicketRepository ticketRepository;
    private final AuthCodePersister authCodeIssuer;

    public ConsentResponse consent(ConsentRequest request) throws ErrorConsentResponseException {
        var ticket = request.getTicket();

        var authData = ticketRepository.findByKey(ticket);
        if (authData.isEmpty()) {
            var error = RequestError.<ConsentErrorType>builder()
                    .error(ConsentErrorType.TICKET_EXPIRED)
                    .errorDescription("Ticket expired")
                    .state(request.getState())
                    .build();

            throw new ErrorConsentResponseException(error);
        }

        ticketRepository.remove(ticket);

        var authCode = authCodeIssuer.persist(authData.get().getScope());
        return new ConsentResponse(authCode, request.getState(), authData.get().getRedirectUri());
    }
}
