package com.btg.fondos.service;

import com.btg.fondos.enums.TransactionType;
import com.btg.fondos.model.Fund;
import com.btg.fondos.model.Transaction;
import com.btg.fondos.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public Transaction createTransaction(String clientId, Fund fund, TransactionType type) {
        Transaction transaction = Transaction.builder()
                .id(UUID.randomUUID().toString())
                .clientId(clientId)
                .fundId(fund.getId())
                .fundName(fund.getName())
                .type(type)
                .amount(fund.getMinimumAmount())
                .build();

        return transactionRepository.save(transaction);
    }

    public List<Transaction> getTransactionsByClientId(String clientId) {
        return transactionRepository.findByClientIdOrderByTimestampDesc(clientId);
    }
}
