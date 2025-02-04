package ru.gr5140904_30201.kichu.util;

import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import ru.gr5140904_30201.kichu.view.MainLayout;

public interface NavbarUpdatable extends BeforeEnterObserver {
    @Override
    default void beforeEnter(BeforeEnterEvent event) {
        // Получаем текущий layout и обновляем его
        MainLayout layout = (MainLayout) event.getUI().getChildren()
                .filter(component -> component instanceof MainLayout)
                .findFirst()
                .orElse(null);

        if (layout != null) {
            layout.updateNavbar();
        }
    }
}
