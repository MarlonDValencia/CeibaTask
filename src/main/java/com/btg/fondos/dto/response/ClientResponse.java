package com.btg.fondos.dto.response;

import com.btg.fondos.enums.NotificationType;
import com.btg.fondos.model.Client;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ClientResponse {

    private String id;
    private String name;
    private String email;
    private String phone;
    private double balance;
    private NotificationType notificationPreference;
    private List<String> subscribedFunds;

    public static ClientResponse from(Client client) {
        return ClientResponse.builder()
                .id(client.getId())
                .name(client.getName())
                .email(client.getEmail())
                .phone(client.getPhone())
                .balance(client.getBalance())
                .notificationPreference(client.getNotificationPreference())
                .subscribedFunds(client.getSubscribedFunds())
                .build();
    }
}
