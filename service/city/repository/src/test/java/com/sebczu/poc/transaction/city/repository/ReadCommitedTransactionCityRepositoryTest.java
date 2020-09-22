package com.sebczu.poc.transaction.city.repository;

import com.sebczu.poc.transaction.city.repository.entity.CityEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class ReadCommitedTransactionCityRepositoryTest extends CityRepositoryTest {

    @Autowired
    private CityService service;

    @Test
    void readCommited() throws ExecutionException, InterruptedException {
        CityEntity city = repository.save(CityFactory.create());

        CompletableFuture<CityEntity> readCommited = CompletableFuture.supplyAsync(() -> service.readCommited(city.getId()));
        CompletableFuture<CityEntity> addPopulation = CompletableFuture.supplyAsync(() -> service.addPopulation(city.getId()));

        CityEntity result = readCommited.get();
        addPopulation.get();

        assertThat(repository.findAll())
                .hasSize(1)
                .element(0)
                .extracting(CityEntity::getPopulation).isEqualTo(result.getPopulation());
    }


}
