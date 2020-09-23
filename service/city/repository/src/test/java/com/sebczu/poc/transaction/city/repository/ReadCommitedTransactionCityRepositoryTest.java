package com.sebczu.poc.transaction.city.repository;

import com.sebczu.poc.transaction.city.repository.entity.CityEntity;
import com.sebczu.poc.transaction.city.repository.factory.CityFactory;
import com.sebczu.poc.transaction.city.repository.service.ReadCommitedCityService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;

class ReadCommitedTransactionCityRepositoryTest extends CityRepositoryTest {

    @Autowired
    private ReadCommitedCityService service;

    @Test
    @DisplayName("TransactionReadCommited(start transaction) -> SecondTransaction(add population) -> TransactionReadCommited(read commited population)")
    void startTransaction_addPopulation_readPopulation() throws ExecutionException, InterruptedException {
        CityEntity city = repository.save(CityFactory.create());

        CompletableFuture<CityEntity> readCommited = CompletableFuture.supplyAsync(() -> service.wait_readAndAddPopulation(city.getId()));
        CompletableFuture<CityEntity> addPopulation = CompletableFuture.supplyAsync(() -> service.addPopulation(city.getId()));

        readCommited.get();
        addPopulation.get();

        assertThat(repository.findAll())
                .hasSize(1)
                .element(0)
                .extracting(CityEntity::getPopulation).isEqualTo(2200);
    }

    @Test
    @DisplayName("TransactionReadCommited(read population) -> SecondTransaction(add population) -> TransactionReadCommited(add population)")
    void readPopulationT1_addPopulationT2_addPopulationT1() throws ExecutionException, InterruptedException {
        CityEntity city = repository.save(CityFactory.create());

        CompletableFuture<CityEntity> readCommited = CompletableFuture.supplyAsync(() -> service.readPopulation_wait_addPopulation(city.getId()));
        CompletableFuture<CityEntity> addPopulation = CompletableFuture.supplyAsync(() -> service.addPopulation(city.getId()));

        readCommited.get();
        addPopulation.get();

        assertThat(repository.findAll())
                .hasSize(1)
                .element(0)
                .extracting(CityEntity::getPopulation).isEqualTo(2100);
    }

    @Test
    @DisplayName("TransactionReadCommited(read population) -> SecondTransaction(add population) -> TransactionReadCommited(read and add population)")
    void readPopulationT1_addPopulationT2_readAndAddPopulationT1() throws ExecutionException, InterruptedException {
        CityEntity city = repository.save(CityFactory.create());

        CompletableFuture<CityEntity> readCommited = CompletableFuture.supplyAsync(() -> service.readPopulation_wait_readAndAddPopulation(city.getId()));
        CompletableFuture<CityEntity> addPopulation = CompletableFuture.supplyAsync(() -> service.addPopulation(city.getId()));

        readCommited.get();
        addPopulation.get();

        assertThat(repository.findAll())
                .hasSize(1)
                .element(0)
                .extracting(CityEntity::getPopulation).isEqualTo(2100);
    }
}
