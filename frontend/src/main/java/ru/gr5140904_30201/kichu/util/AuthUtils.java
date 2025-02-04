package ru.gr5140904_30201.kichu.util;

import com.vaadin.flow.server.VaadinSession;
import ru.gr5140904_30201.kichu.model.User;

public class AuthUtils {
    private static final String TOKEN_ATTRIBUTE = "token";
    private static final String EMAIL_ATTRIBUTE = "email";
    private static final String USER_ATTRIBUTE = "user";
    private AuthUtils() {
    }

    public static void saveUser(User user) {
        VaadinSession.getCurrent().setAttribute(USER_ATTRIBUTE, user);
    }

    public static User getUser() {
        return (User) VaadinSession.getCurrent().getAttribute(USER_ATTRIBUTE);
    }
}
