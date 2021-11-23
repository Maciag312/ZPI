package com.zpi.domain.rest.ams;

import java.util.Optional;

public interface AmsService {
    Optional<Client> clientDetails(String id);
    boolean isAuthenticated(User User);
    AuthConfiguration config();
    UserInfo userInfo(String email);
}
