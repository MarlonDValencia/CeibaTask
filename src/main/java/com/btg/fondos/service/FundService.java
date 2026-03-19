package com.btg.fondos.service;

import com.btg.fondos.enums.TransactionType;
import com.btg.fondos.exception.AlreadySubscribedException;
import com.btg.fondos.exception.FundNotFoundException;
import com.btg.fondos.exception.InsufficientBalanceException;
import com.btg.fondos.exception.NotSubscribedException;
import com.btg.fondos.model.Client;
import com.btg.fondos.model.Fund;
import com.btg.fondos.model.Transaction;
import com.btg.fondos.repository.FundRepository;
import com.btg.fondos.service.notification.NotificationFactory;
import com.btg.fondos.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FundService {

    private final FundRepository fundRepository;
    private final ClientService clientService;
    private final TransactionService transactionService;
    private final NotificationFactory notificationFactory;

    public List<Fund> getAllFunds() {
        return fundRepository.findAll();
    }

    public Transaction subscribe(String clientId, String fundId) {
        Fund fund = findFundById(fundId);
        Client client = clientService.findById(clientId);

        validateNotAlreadySubscribed(client, fund);
        validateSufficientBalance(client, fund);

        client.setBalance(client.getBalance() - fund.getMinimumAmount());
        client.getSubscribedFunds().add(fund.getId());
        clientService.save(client);

        Transaction transaction = transactionService.createTransaction(clientId, fund, TransactionType.APERTURA);

        sendNotification(client, fund, TransactionType.APERTURA);

        return transaction;
    }

    public Transaction unsubscribe(String clientId, String fundId) {
        Fund fund = findFundById(fundId);
        Client client = clientService.findById(clientId);

        validateIsSubscribed(client, fund);

        client.setBalance(client.getBalance() + fund.getMinimumAmount());
        client.getSubscribedFunds().remove(fund.getId());
        clientService.save(client);

        return transactionService.createTransaction(clientId, fund, TransactionType.CANCELACION);
    }

    private Fund findFundById(String fundId) {
        return fundRepository.findById(fundId)
                .orElseThrow(() -> new FundNotFoundException(fundId));
    }

    private void validateNotAlreadySubscribed(Client client, Fund fund) {
        if (client.getSubscribedFunds().contains(fund.getId())) {
            throw new AlreadySubscribedException(fund.getName());
        }
    }

    private void validateIsSubscribed(Client client, Fund fund) {
        if (!client.getSubscribedFunds().contains(fund.getId())) {
            throw new NotSubscribedException(fund.getName());
        }
    }

    private void validateSufficientBalance(Client client, Fund fund) {
        if (client.getBalance() < fund.getMinimumAmount()) {
            throw new InsufficientBalanceException(fund.getName());
        }
    }

    private void sendNotification(Client client, Fund fund, TransactionType type) {
        NotificationService notificationService = notificationFactory.getService(client.getNotificationPreference());
        notificationService.notify(client, fund, type);
    }
}
