package com.zpi.domain.organization.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientService {
    private final ClientRepository clientRepository;

    public boolean saveClient(Client client) {
        var foundClient = clientRepository.findByKey(client.getId());

        if (foundClient.isPresent())
            return false;

        clientRepository.save(client.getId(), client);
        return true;
    }
}
