package com.zpi.domain.authCode.authorizationRequest;

import com.zpi.domain.rest.ams.User;

public interface MailService {
    void send(String code, User user);
}
