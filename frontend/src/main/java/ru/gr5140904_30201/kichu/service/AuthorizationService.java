package ru.gr5140904_30201.kichu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.gr5140904_30201.kichu.model.User;
import ru.gr5140904_30201.kichu.service.client.RealtyApiClient;

@Service
@RequiredArgsConstructor
public class AuthorizationService {
    private final RealtyApiClient realtyApiClient;

    public String register(String name, String email, String role, String password) {
        User user = User.builder()
                .name(name)
                .email(email)
                .password(password)
                .userRole(role)
                .build();

       return realtyApiClient.register(user);
    }

    public User login(String email, String password) {
        User user = User.builder()
                .email(email)
                .password(password)
                .userRole("user")
                .build();

        return realtyApiClient.login(user);
    }
}

