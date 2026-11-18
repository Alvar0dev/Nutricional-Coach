package com.nutricionalcoach.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.Banner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Profiles;

import static org.assertj.core.api.Assertions.assertThat;

class ProdProfileConfigTest {

    @Test
    void postgresqlDriverIsAvailableOnRuntimeClasspath() throws ClassNotFoundException {
        Class<?> driverClass = Class.forName("org.postgresql.Driver");
        assertThat(driverClass).isNotNull();
    }

    @Test
    void resolvesPostgresRdsConfigurationAndDisablesH2ConsoleInProdProfile() {
        try (ConfigurableApplicationContext context = new SpringApplicationBuilder(EmptyConfig.class)
                .bannerMode(Banner.Mode.OFF)
                .web(WebApplicationType.NONE)
                .profiles("prod")
                .run(
                        "--DB_URL=jdbc:postgresql://rds-nutri.eu-west-1.rds.amazonaws.com:5432/nutricionaldb",
                        "--DB_USER=coach_prod",
                        "--DB_PASSWORD=rds-secret-pass"
                )) {

            ConfigurableEnvironment environment = context.getEnvironment();
            assertThat(environment.acceptsProfiles(Profiles.of("prod"))).isTrue();
            assertThat(environment.acceptsProfiles(Profiles.of("local"))).isFalse();
            assertThat(environment.getProperty("spring.h2.console.enabled", Boolean.class)).isFalse();
            assertThat(environment.getProperty("spring.jpa.hibernate.ddl-auto")).isEqualTo("validate");
            assertThat(environment.getProperty("spring.jpa.show-sql", Boolean.class)).isFalse();
            assertThat(environment.getProperty("spring.jpa.open-in-view", Boolean.class)).isFalse();

            DataSourceProperties dataSourceProperties = Binder.get(environment)
                    .bindOrCreate("spring.datasource", DataSourceProperties.class);
            assertThat(dataSourceProperties.getUrl())
                    .isEqualTo("jdbc:postgresql://rds-nutri.eu-west-1.rds.amazonaws.com:5432/nutricionaldb");
            assertThat(dataSourceProperties.getDriverClassName()).isEqualTo("org.postgresql.Driver");
            assertThat(dataSourceProperties.getUsername()).isEqualTo("coach_prod");
            assertThat(dataSourceProperties.getPassword()).isEqualTo("rds-secret-pass");
        }
    }

    @Configuration
    static class EmptyConfig {
    }
}
