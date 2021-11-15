package com.zpi.domain.authCode.authorizationRequest;

import com.zpi.domain.authCode.authenticationRequest.AuthenticationRequest;
import com.zpi.domain.authCode.consentRequest.TicketData;
import com.zpi.domain.authCode.consentRequest.TicketRepository;
import com.zpi.domain.common.CodeGenerator;
import com.zpi.domain.rest.ams.User;
import com.zpi.domain.rest.analysis.AnalysisService;
import com.zpi.domain.rest.analysis.afterLogin.AnalysisRequest;
import com.zpi.domain.twoFactorAuth.TwoFactorData;
import com.zpi.domain.twoFactorAuth.TwoFactorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthorizationServiceImpl implements AuthorizationService {
    private final TicketRepository repository;
    private final CodeGenerator generator;
    private final AnalysisService analysisService;
    private final TwoFactorRepository twoFactorRepository;
    private final MailService mail;

    @Override
    public AuthorizationResponse createTicket(User user, AuthenticationRequest request, AnalysisRequest analysisRequest) {
        var ticket = generator.ticketCode();
        var authData = getAuthData(user, request);
        repository.save(ticket, authData);

        if (analysisService.isAdditionalLayerRequired(analysisRequest)) {
            return new AuthorizationResponse(saveTwoFactorCode(ticket, user), TicketType.TICKET_2FA, request.getState());
        }

        return new AuthorizationResponse(ticket, TicketType.TICKET, request.getState());
    }

    private TicketData getAuthData(User user, AuthenticationRequest request) {
        var redirectUri = request.getRedirectUri();
        var scope = request.getScope();
        var username = user.getEmail();
        return new TicketData(redirectUri, scope, username);
    }

    private String saveTwoFactorCode(String ticket, User user) {
        var twoFactorKey = generator.ticketCode();
        var twoFactorCode = generator.twoFactorCode();
        mail.send(twoFactorCode, user);
        var data = new TwoFactorData(ticket, twoFactorCode);
        twoFactorRepository.save(twoFactorKey, data);

        return twoFactorKey;
    }
}
