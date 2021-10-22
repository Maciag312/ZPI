package com.zpi.domain.rest.ams;

import java.util.Optional;

public interface AmsService {
    Optional<Client> clientDetails(String id);
    boolean registerUser(User user);
    boolean isAuthenticated(User User);
}
