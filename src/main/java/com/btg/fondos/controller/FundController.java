package com.btg.fondos.controller;

import com.btg.fondos.dto.response.SubscriptionResponse;
import com.btg.fondos.model.Client;
import com.btg.fondos.model.Fund;
import com.btg.fondos.model.Transaction;
import com.btg.fondos.service.ClientService;
import com.btg.fondos.service.FundService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/funds")
@RequiredArgsConstructor
public class FundController {

    private final FundService fundService;
    private final ClientService clientService;

    @GetMapping
    public ResponseEntity<List<Fund>> getAllFunds() {
        return ResponseEntity.ok(fundService.getAllFunds());
    }

    @PostMapping("/{fundId}/subscribe")
    public ResponseEntity<SubscriptionResponse> subscribe(
            @PathVariable String fundId,
            Authentication authentication) {

        String clientId = authentication.getName();
        Transaction transaction = fundService.subscribe(clientId, fundId);
        Client client = clientService.findById(clientId);

        SubscriptionResponse response = SubscriptionResponse.builder()
                .transactionId(transaction.getId())
                .fundName(transaction.getFundName())
                .type(transaction.getType().name())
                .amount(transaction.getAmount())
                .newBalance(client.getBalance())
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{fundId}/unsubscribe")
    public ResponseEntity<SubscriptionResponse> unsubscribe(
            @PathVariable String fundId,
            Authentication authentication) {

        String clientId = authentication.getName();
        Transaction transaction = fundService.unsubscribe(clientId, fundId);
        Client client = clientService.findById(clientId);

        SubscriptionResponse response = SubscriptionResponse.builder()
                .transactionId(transaction.getId())
                .fundName(transaction.getFundName())
                .type(transaction.getType().name())
                .amount(transaction.getAmount())
                .newBalance(client.getBalance())
                .build();

        return ResponseEntity.ok(response);
    }
}
