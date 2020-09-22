package com.sebczu.poc.transaction.city.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;

@Slf4j
@SpringBootTest
@ContextConfiguration(initializers = {CityRepositoryTest.ContextInitializer.class})
@EnableJpaRepositories(basePackages = "com.sebczu")
abstract class CityRepositoryTest {

    @Autowired
    protected CityRepository repository;

    @BeforeEach
    @AfterEach
    private void cleanRepository() {
        repository.deleteAll();
    }

    static class ContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext applicationContext) {
            log.info("context init");
            PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:13")
                    .withDatabaseName("test")
                    .withUsername("admin")
                    .withPassword("test");

            postgreSQLContainer.start();

            TestPropertyValues.of(
                    "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
                    "spring.datasource.username=" + postgreSQLContainer.getUsername(),
                    "spring.datasource.password=" + postgreSQLContainer.getPassword()
            ).applyTo(applicationContext);
        }
    }

}
