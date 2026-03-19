package com.btg.fondos.dto.response;

import com.btg.fondos.enums.TransactionType;
import com.btg.fondos.model.Transaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class TransactionResponse {

    private String id;
    private String fundId;
    private String fundName;
    private TransactionType type;
    private double amount;
    private LocalDateTime timestamp;

    public static TransactionResponse from(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .fundId(transaction.getFundId())
                .fundName(transaction.getFundName())
                .type(transaction.getType())
                .amount(transaction.getAmount())
                .timestamp(transaction.getTimestamp())
                .build();
    }
}
