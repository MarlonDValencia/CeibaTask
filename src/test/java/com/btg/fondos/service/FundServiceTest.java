package com.btg.fondos.service;

import com.btg.fondos.enums.FundCategory;
import com.btg.fondos.enums.NotificationType;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FundServiceTest {

    @Mock
    private FundRepository fundRepository;

    @Mock
    private ClientService clientService;

    @Mock
    private TransactionService transactionService;

    @Mock
    private NotificationFactory notificationFactory;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private FundService fundService;

    private Client client;
    private Fund fund;

    @BeforeEach
    void setUp() {
        client = Client.builder()
                .id("client-1")
                .name("Test User")
                .email("test@email.com")
                .balance(500000)
                .notificationPreference(NotificationType.EMAIL)
                .subscribedFunds(new ArrayList<>())
                .build();

        fund = Fund.builder()
                .id("1")
                .name("FPV_BTG_PACTUAL_RECAUDADORA")
                .minimumAmount(75000)
                .category(FundCategory.FPV)
                .build();
    }

    @Test
    void subscribe_shouldDeductBalanceAndCreateTransaction() {
        when(fundRepository.findById("1")).thenReturn(Optional.of(fund));
        when(clientService.findById("client-1")).thenReturn(client);
        when(clientService.save(any(Client.class))).thenReturn(client);
        when(notificationFactory.getService(NotificationType.EMAIL)).thenReturn(notificationService);

        Transaction expected = Transaction.builder()
                .id("tx-1")
                .clientId("client-1")
                .fundId("1")
                .fundName(fund.getName())
                .type(TransactionType.APERTURA)
                .amount(75000)
                .build();
        when(transactionService.createTransaction(eq("client-1"), eq(fund), eq(TransactionType.APERTURA)))
                .thenReturn(expected);

        Transaction result = fundService.subscribe("client-1", "1");

        assertThat(result.getType()).isEqualTo(TransactionType.APERTURA);
        assertThat(client.getBalance()).isEqualTo(425000);
        assertThat(client.getSubscribedFunds()).contains("1");
        verify(clientService).save(client);
        verify(notificationService).notify(client, fund, TransactionType.APERTURA);
    }

    @Test
    void subscribe_shouldThrowWhenFundNotFound() {
        when(fundRepository.findById("99")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fundService.subscribe("client-1", "99"))
                .isInstanceOf(FundNotFoundException.class);
    }

    @Test
    void subscribe_shouldThrowWhenAlreadySubscribed() {
        client.getSubscribedFunds().add("1");
        when(fundRepository.findById("1")).thenReturn(Optional.of(fund));
        when(clientService.findById("client-1")).thenReturn(client);

        assertThatThrownBy(() -> fundService.subscribe("client-1", "1"))
                .isInstanceOf(AlreadySubscribedException.class);
    }

    @Test
    void subscribe_shouldThrowWhenInsufficientBalance() {
        client.setBalance(50000);
        when(fundRepository.findById("1")).thenReturn(Optional.of(fund));
        when(clientService.findById("client-1")).thenReturn(client);

        assertThatThrownBy(() -> fundService.subscribe("client-1", "1"))
                .isInstanceOf(InsufficientBalanceException.class)
                .hasMessageContaining("FPV_BTG_PACTUAL_RECAUDADORA");
    }

    @Test
    void unsubscribe_shouldRestoreBalanceAndCreateTransaction() {
        client.getSubscribedFunds().add("1");
        client.setBalance(425000);

        when(fundRepository.findById("1")).thenReturn(Optional.of(fund));
        when(clientService.findById("client-1")).thenReturn(client);
        when(clientService.save(any(Client.class))).thenReturn(client);

        Transaction expected = Transaction.builder()
                .id("tx-2")
                .clientId("client-1")
                .fundId("1")
                .fundName(fund.getName())
                .type(TransactionType.CANCELACION)
                .amount(75000)
                .build();
        when(transactionService.createTransaction(eq("client-1"), eq(fund), eq(TransactionType.CANCELACION)))
                .thenReturn(expected);

        Transaction result = fundService.unsubscribe("client-1", "1");

        assertThat(result.getType()).isEqualTo(TransactionType.CANCELACION);
        assertThat(client.getBalance()).isEqualTo(500000);
        assertThat(client.getSubscribedFunds()).doesNotContain("1");
        verify(clientService).save(client);
    }

    @Test
    void unsubscribe_shouldThrowWhenNotSubscribed() {
        when(fundRepository.findById("1")).thenReturn(Optional.of(fund));
        when(clientService.findById("client-1")).thenReturn(client);

        assertThatThrownBy(() -> fundService.unsubscribe("client-1", "1"))
                .isInstanceOf(NotSubscribedException.class);
    }

    @Test
    void getAllFunds_shouldReturnAllFunds() {
        List<Fund> funds = List.of(fund);
        when(fundRepository.findAll()).thenReturn(funds);

        List<Fund> result = fundService.getAllFunds();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("FPV_BTG_PACTUAL_RECAUDADORA");
    }
}
