package com.zpi.domain.twoFactorAuth;

import com.zpi.api.common.dto.ErrorResponseDTO;
import com.zpi.api.common.exception.ErrorResponseException;
import com.zpi.domain.authCode.consentRequest.TicketRepository;
import com.zpi.domain.common.RequestError;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TwoFactorAuthServiceImpl implements TwoFactorAuthService {
    private final TwoFactorRepository repository;

    @Override
    public TwoFactorAuthResponse validate(TwoFactorData data) throws ErrorResponseException {
        var ticket = authenticationTicket(data);
        if (ticket == null) {
            var error = RequestError.<TwoFactorErrorType>builder()
                    .error(TwoFactorErrorType.INCORRECT_2FA_CODE)
                    .errorDescription("Incorrect 2FA code")
                    .build();
            throw new ErrorResponseException(new ErrorResponseDTO<>(error, ""));
        }

        return new TwoFactorAuthResponse(ticket);
    }

    private String authenticationTicket(TwoFactorData data) {
        var toCheck = repository.findByKey(data.getTicket()).orElse(null);
        return isTwoFactorCodeValid(data, toCheck) ? toCheck.getTicket() : null;
    }

    private boolean isTwoFactorCodeValid(TwoFactorData data, TwoFactorData toCheck) {
        return toCheck != null && toCheck.getTwoFactorCode().equals(data.getTwoFactorCode());
    }
}
