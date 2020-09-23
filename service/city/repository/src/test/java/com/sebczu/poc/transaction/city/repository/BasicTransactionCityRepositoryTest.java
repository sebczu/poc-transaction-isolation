package com.sebczu.poc.transaction.city.repository;

import com.sebczu.poc.transaction.city.repository.factory.CityFactory;
import com.sebczu.poc.transaction.city.repository.service.BasicCityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class BasicTransactionCityRepositoryTest extends CityRepositoryTest {

    @Autowired
    private BasicCityService service;

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
