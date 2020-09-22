package com.sebczu.poc.transaction.city.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@Slf4j
class BasicTransactionCityRepositoryTest extends CityRepositoryTest {

    @Autowired
    private CityService service;

    @Test
    void simpleTransaction() {
        repository.save(CityFactory.create());

        assertThat(repository.findAll())
                .hasSize(1);
    }

    @Test
    void throwExceptionInTransaction() {
        Throwable thrown = catchThrowable(() -> service.throwException());

        assertThat(thrown)
                .isInstanceOf(RuntimeException.class);
        assertThat(repository.findAll())
                .hasSize(0);
    }

}
