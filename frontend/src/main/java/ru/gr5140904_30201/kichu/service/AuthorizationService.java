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
        try {
            return realtyApiClient.register(user);
        } catch (Exception e) {
            if (e.getMessage().equals("409 Conflict from POST http://localhost:8080/api/auth/register")) {
                return "Email already in use";
            }
            return "Internal error, try again";
        }
    }

    public User login(String email, String password) {
        User user = User.builder()
                .email(email)
                .password(password)
                .userRole("user")
                .build();

        try {
            return realtyApiClient.login(user);
        } catch (Exception e) {
            return null;
        }
    }
}

