package ru.gr5140904_30201.kichu.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.RouterLink;
import ru.gr5140904_30201.kichu.util.AuthUtils;
import ru.gr5140904_30201.kichu.view.client.ClientBookingView;
import ru.gr5140904_30201.kichu.view.owner.OwnerBookingView;
import ru.gr5140904_30201.kichu.view.owner.RealtyView;

import java.util.ArrayList;
import java.util.List;

import static ru.gr5140904_30201.kichu.model.enums.Role.ADMIN;
import static ru.gr5140904_30201.kichu.model.enums.Role.USER;

public class MainLayout extends AppLayout {
    private HorizontalLayout header;
    private List<Component> components;

    public MainLayout() {
        updateNavbar();
    }

    public void updateNavbar() {
        clearNavbar();
        header = new HorizontalLayout();
        H1 logo = new H1("Real Estate Management");
        logo.getStyle().set("margin", "0").set("font-size", "var(--lumo-font-size-xl)");
        components = new ArrayList<>();
        components.add(logo);
        if (AuthUtils.getUser() == null) {
            RouterLink registerLink = new RouterLink("Register", RegistrationView.class);
            RouterLink loginLink = new RouterLink("Login", LoginView.class);
            components.add(registerLink);
            components.add(loginLink);
        } else {
            Button logoutButton = new Button("Logout", event -> {
                AuthUtils.saveUser(null);
                UI.getCurrent().navigate("login");
                updateNavbar();
            });
            if (AuthUtils.getUser().getUserRole().equals(ADMIN)) {
                components.add(new RouterLink("Realty", RealtyView.class));
                components.add(new RouterLink("Bookings", OwnerBookingView.class));
            } else if (AuthUtils.getUser().getUserRole().equals(USER)) {
                components.add(new RouterLink("Booking", ClientBookingView.class));
            }
            components.add(logoutButton);
        }
        header = new HorizontalLayout(components.toArray(Component[]::new));
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidth("100%");
        header.expand(logo);
        header.getStyle().set("padding", "0.5em");
        addToNavbar(header);
    }

    private void clearNavbar() {
        if (header != null && components != null) {
            header.remove(components.toArray(Component[]::new));
            remove(header);
        }
    }
}
