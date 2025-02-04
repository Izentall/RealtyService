package ru.gr5140904_30201.kichu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.orm.jpa.HibernateMetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JndiDataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.XADataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizationAutoConfiguration;
import org.springframework.context.annotation.Import;
import ru.gr5140904_30201.kichu.conf.Config;
import ru.gr5140904_30201.kichu.conf.WebConfig;

@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        JndiDataSourceAutoConfiguration.class,
        XADataSourceAutoConfiguration.class,
        TransactionManagerCustomizationAutoConfiguration.class,
        TransactionAutoConfiguration.class,
        HibernateMetricsAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
@Import(value = {
        Config.class,
        WebConfig.class
})
public class RealtyApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(RealtyApiApplication.class, args);
    }
}
