package com.zpi.domain.rest.ams;

import java.util.Optional;

public interface AmsService {
    Optional<Client> clientDetails(String id);
}
