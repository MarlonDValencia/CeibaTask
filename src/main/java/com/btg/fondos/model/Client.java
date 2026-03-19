package com.btg.fondos.model;

import com.btg.fondos.enums.NotificationType;
import com.btg.fondos.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "clients")
public class Client {

    @Id
    private String id;

    private String name;

    @Indexed(unique = true)
    private String email;

    private String phone;
    private String password;

    private double balance;

    @Builder.Default
    private NotificationType notificationPreference = NotificationType.EMAIL;

    @Builder.Default
    private Role role = Role.CLIENT;

    @Builder.Default
    private List<String> subscribedFunds = new ArrayList<>();

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
