package com.btg.fondos.service;

import com.btg.fondos.exception.ClientNotFoundException;
import com.btg.fondos.model.Client;
import com.btg.fondos.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;

    public Client findById(String clientId) {
        return clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException(clientId));
    }

    public Client findByEmail(String email) {
        return clientRepository.findByEmail(email)
                .orElseThrow(() -> new ClientNotFoundException(email));
    }

    public Client save(Client client) {
        return clientRepository.save(client);
    }
}
