package com.btg.fondos.service;

import com.btg.fondos.enums.FundCategory;
import com.btg.fondos.enums.TransactionType;
import com.btg.fondos.model.Fund;
import com.btg.fondos.model.Transaction;
import com.btg.fondos.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void createTransaction_shouldSaveWithCorrectData() {
        Fund fund = Fund.builder()
                .id("1")
                .name("FPV_BTG_PACTUAL_RECAUDADORA")
                .minimumAmount(75000)
                .category(FundCategory.FPV)
                .build();

        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Transaction result = transactionService.createTransaction("client-1", fund, TransactionType.APERTURA);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getClientId()).isEqualTo("client-1");
        assertThat(result.getFundId()).isEqualTo("1");
        assertThat(result.getFundName()).isEqualTo("FPV_BTG_PACTUAL_RECAUDADORA");
        assertThat(result.getType()).isEqualTo(TransactionType.APERTURA);
        assertThat(result.getAmount()).isEqualTo(75000);

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(captor.capture());
        assertThat(captor.getValue().getClientId()).isEqualTo("client-1");
    }

    @Test
    void getTransactionsByClientId_shouldReturnOrderedTransactions() {
        List<Transaction> transactions = List.of(
                Transaction.builder().id("tx-1").clientId("client-1").type(TransactionType.APERTURA).build(),
                Transaction.builder().id("tx-2").clientId("client-1").type(TransactionType.CANCELACION).build()
        );

        when(transactionRepository.findByClientIdOrderByTimestampDesc("client-1")).thenReturn(transactions);

        List<Transaction> result = transactionService.getTransactionsByClientId("client-1");

        assertThat(result).hasSize(2);
        verify(transactionRepository).findByClientIdOrderByTimestampDesc("client-1");
    }
}
