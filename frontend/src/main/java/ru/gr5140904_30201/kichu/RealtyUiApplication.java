package ru.gr5140904_30201.kichu;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import ru.gr5140904_30201.kichu.conf.Config;

@SpringBootApplication
@Import(Config.class)
@EnableAsync
@Theme(variant = Lumo.DARK)
public class RealtyUiApplication implements AppShellConfigurator {
    public static void main(String[] args) {
        SpringApplication.run(RealtyUiApplication.class, args);
    }
}
