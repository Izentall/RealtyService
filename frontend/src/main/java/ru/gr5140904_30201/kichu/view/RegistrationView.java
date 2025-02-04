package ru.gr5140904_30201.kichu.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import ru.gr5140904_30201.kichu.service.AuthorizationService;
import ru.gr5140904_30201.kichu.util.NavbarUpdatable;

import java.util.List;

import static ru.gr5140904_30201.kichu.model.enums.Role.ADMIN;
import static ru.gr5140904_30201.kichu.model.enums.Role.USER;

@Route(value = "register", layout = MainLayout.class)
@PageTitle("Register | Real Estate Management")
public class RegistrationView extends VerticalLayout implements NavbarUpdatable {

    public RegistrationView(@Autowired AuthorizationService authorizationService) {
        TextField nameField = new TextField("Full Name");
        EmailField emailField = new EmailField("Email");
        ComboBox<String> roleField = new ComboBox<>("Role");
        roleField.setItems(List.of(USER, ADMIN));
        PasswordField passwordField = new PasswordField("Password");

        Button registerButton = new Button("Register", event -> {
            System.out.println(authorizationService.register(nameField.getValue(), emailField.getValue(), roleField.getValue(), passwordField.getValue()));
            Notification.show("Registration successful!");
        });

        add(nameField, emailField, roleField, passwordField, registerButton);
        setHorizontalComponentAlignment(Alignment.CENTER);
        setAlignItems(Alignment.CENTER);
    }
}

