package com.btg.fondos.controller;

import com.btg.fondos.dto.response.ClientResponse;
import com.btg.fondos.model.Client;
import com.btg.fondos.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @GetMapping("/me")
    public ResponseEntity<ClientResponse> getProfile(Authentication authentication) {
        String clientId = authentication.getName();
        Client client = clientService.findById(clientId);
        return ResponseEntity.ok(ClientResponse.from(client));
    }
}
