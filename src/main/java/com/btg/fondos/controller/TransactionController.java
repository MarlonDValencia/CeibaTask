package com.btg.fondos.controller;

import com.btg.fondos.dto.response.TransactionResponse;
import com.btg.fondos.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getTransactions(Authentication authentication) {
        String clientId = authentication.getName();

        List<TransactionResponse> transactions = transactionService
                .getTransactionsByClientId(clientId)
                .stream()
                .map(TransactionResponse::from)
                .toList();

        return ResponseEntity.ok(transactions);
    }
}
