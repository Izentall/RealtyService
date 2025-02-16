package ru.gr5140904_30201.kichu.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Autowired;
import ru.gr5140904_30201.kichu.model.User;
import ru.gr5140904_30201.kichu.service.AuthorizationService;
import ru.gr5140904_30201.kichu.util.AuthUtils;
import ru.gr5140904_30201.kichu.util.NavbarUpdatable;

import static ru.gr5140904_30201.kichu.model.enums.Role.ADMIN;
import static ru.gr5140904_30201.kichu.model.enums.Role.USER;

@Route(value = "login", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PageTitle("Login | Real Estate Management")
public class LoginView extends VerticalLayout implements NavbarUpdatable {
    private MainLayout layout;

    public LoginView(@Autowired AuthorizationService authorizationService) {
        EmailField emailField = new EmailField("Email");
        PasswordField passwordField = new PasswordField("Password");

        Button loginButton = new Button("Login", event -> {
            String email = emailField.getValue();
            User user = authorizationService.login(email, passwordField.getValue());
            if (user == null) {
                Notification.show("Wrong email or password");
                return;
            }
            AuthUtils.saveUser(user);
            layout.updateNavbar();
            Notification.show("Login successful!");
            if (user.getUserRole().equals(ADMIN)) {
                UI.getCurrent().navigate("realty");
            } else if (AuthUtils.getUser().getUserRole().equals(USER)) {
                UI.getCurrent().navigate("booking");
            }
        });

        add(emailField, passwordField, loginButton);
        setHorizontalComponentAlignment(Alignment.CENTER);
        setAlignItems(Alignment.CENTER);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Получаем текущий layout и обновляем его
        layout = (MainLayout) event.getUI().getChildren()
                .filter(component -> component instanceof MainLayout)
                .findFirst()
                .orElse(null);

        if (layout != null) {
            layout.updateNavbar();
        }
    }
}
