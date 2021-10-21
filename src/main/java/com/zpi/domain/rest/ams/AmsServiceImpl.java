package com.zpi.domain.rest.ams;

import com.zpi.infrastructure.rest.ams.AmsClient;
import com.zpi.infrastructure.rest.ams.ClientDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class AmsServiceImpl implements AmsService {
    private final AmsClient client;

    @Override
    public Optional<Client> clientDetails(String id) {
        return client.clientDetails(id).map(ClientDTO::toDomain);
    }
}
