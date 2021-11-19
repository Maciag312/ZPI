package com.zpi.domain.authCode.authorizationRequest;

import com.zpi.domain.authCode.authenticationRequest.AuthenticationRequest;
import com.zpi.domain.authCode.authenticationRequest.AuthenticationRequestErrorType;
import com.zpi.domain.authCode.consentRequest.TicketData;
import com.zpi.domain.authCode.consentRequest.TicketRepository;
import com.zpi.domain.common.CodeGenerator;
import com.zpi.domain.common.RequestError;
import com.zpi.domain.rest.ams.AmsService;
import com.zpi.domain.rest.ams.User;
import com.zpi.domain.rest.analysis.AnalysisResponse;
import com.zpi.domain.rest.analysis.AnalysisService;
import com.zpi.domain.rest.analysis.lockout.LoginAction;
import com.zpi.domain.rest.analysis.twoFactor.AnalysisRequest;
import com.zpi.domain.twoFactorAuth.TwoFactorData;
import com.zpi.domain.twoFactorAuth.TwoFactorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {
    private final TicketRepository repository;
    private final CodeGenerator generator;
    private final AnalysisService analysisService;
    private final TwoFactorRepository twoFactorRepository;
    private final MailService mail;
    private final AmsService ams;

    @Override
    public TicketResponse createTicket(User user, AuthenticationRequest request, AnalysisRequest analysisRequest) throws LoginLockoutException, UserValidationFailedException {
        var analysis = analysisService.analyse(analysisRequest);

        if (analysis.getLockout().getAction() == LoginAction.ALLOW) {
            validateUser(user, analysisRequest);
            return handleAllowLogin(user, request, analysis);
        } else {
            var error = RequestError.<AuthenticationRequestErrorType>builder()
                    .error(AuthenticationRequestErrorType.LOGIN_LOCKOUT)
                    .errorDescription(analysis.getLockout().getDelayTill().toString())
                    .build();
            throw new LoginLockoutException(error);
        }
    }

    private TicketResponse handleAllowLogin(User user, AuthenticationRequest request, AnalysisResponse analysis) {
        var ticket = saveTicket(user, request);

        if (analysis.getTwoFactor().isAdditionalLayerRequired()) {
            return new TicketResponse(saveTwoFactorCode(ticket, user), TicketType.TICKET_2FA, request.getState());
        }
        return new TicketResponse(ticket, TicketType.TICKET, request.getState());
    }

    private String saveTicket(User user, AuthenticationRequest request) {
        var ticket = generator.ticketCode();
        var authData = getAuthData(user, request);
        repository.save(ticket, authData);
        return ticket;
    }

    private void validateUser(User user, AnalysisRequest analysisRequest) throws UserValidationFailedException {
        if (!ams.isAuthenticated(user)) {
            var error = RequestError.<AuthenticationRequestErrorType>builder()
                    .error(AuthenticationRequestErrorType.USER_AUTH_FAILED)
                    .errorDescription("User authentication failed")
                    .build();

            analysisService.reportLoginFail(analysisRequest);
            throw new UserValidationFailedException(error);
        }
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
