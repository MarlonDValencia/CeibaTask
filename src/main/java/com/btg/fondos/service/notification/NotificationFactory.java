package com.btg.fondos.service.notification;

import com.btg.fondos.enums.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class NotificationFactory {

    private final Map<String, NotificationService> notificationServices;

    public NotificationService getService(NotificationType type) {
        return switch (type) {
            case EMAIL -> notificationServices.get("emailNotificationService");
            case SMS -> notificationServices.get("smsNotificationService");
        };
    }
}
