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
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.PostgreSQLContainer;

@Slf4j
@SpringBootTest
@ContextConfiguration(initializers = {CityRepositoryTest.PostgresContextInitializer.class})
//@ContextConfiguration(initializers = {CityRepositoryTest.MysqlContextInitializer.class})
@EnableJpaRepositories(basePackages = "com.sebczu")
abstract class CityRepositoryTest {

    @Autowired
    protected CityRepository repository;

    @BeforeEach
    @AfterEach
    private void cleanRepository() {
        repository.deleteAll();
    }

    static class PostgresContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
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

    static class MysqlContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext applicationContext) {
            log.info("context init");
            MySQLContainer  mysqlContainer = new MySQLContainer("mysql:5.7.31")
                    .withDatabaseName("test")
                    .withUsername("admin")
                    .withPassword("test");

            mysqlContainer.start();

            TestPropertyValues.of(
                    "spring.datasource.url=" + mysqlContainer.getJdbcUrl(),
                    "spring.datasource.username=" + mysqlContainer.getUsername(),
                    "spring.datasource.password=" + mysqlContainer.getPassword()
            ).applyTo(applicationContext);
        }
    }
}
