package ru.gr5140904_30201.kichu.util;

import com.vaadin.flow.component.UI;
import ru.gr5140904_30201.kichu.model.User;
import ru.gr5140904_30201.kichu.view.LoginView;

public interface AuthorityRequired {
    default boolean checkRole() {
        User user = AuthUtils.getUser();
        if (user == null) {
            UI.getCurrent().navigate(LoginView.class);
            return false;
        } else if (!getRequiredRole().equals(user.getUserRole())) {
            throw new RuntimeException("No permission");
        }
        return true;
    }

    String getRequiredRole();
}
