package com.btg.fondos.service.notification;

import com.btg.fondos.enums.TransactionType;
import com.btg.fondos.model.Client;
import com.btg.fondos.model.Fund;

public interface NotificationService {

    void notify(Client client, Fund fund, TransactionType type);
}
