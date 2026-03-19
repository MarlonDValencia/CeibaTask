package com.btg.fondos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class SubscriptionResponse {

    private String transactionId;
    private String fundName;
    private String type;
    private double amount;
    private double newBalance;
}
