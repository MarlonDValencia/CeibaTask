package com.btg.fondos.service.notification;

import com.btg.fondos.enums.TransactionType;
import com.btg.fondos.model.Client;
import com.btg.fondos.model.Fund;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service("smsNotificationService")
public class SmsNotificationService implements NotificationService {

    @Override
    public void notify(Client client, Fund fund, TransactionType type) {
        String action = type == TransactionType.APERTURA ? "Suscripción exitosa" : "Cancelación exitosa";
        log.info("[SMS] Enviando a {}: {} al fondo {}", client.getPhone(), action, fund.getName());
    }
}
