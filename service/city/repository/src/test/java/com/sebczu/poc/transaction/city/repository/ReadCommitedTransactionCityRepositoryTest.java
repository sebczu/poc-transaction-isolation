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

    //[2000] - actual population in database

    // T1 ---(startTransaction)--------------------------------------------------------(readPopulation[2100] | addPopulation)---(saveInDB[2200] | endTransaction)
    // T2 ---(startTransaction)---(addPopulation)---(saveInDB[2100] | endTransaction)
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

    // T1 ---(startTransaction)---(readPopulation[2000])---------------------------------------------------------(addPopulation)---(saveInDB[2100] | endTransaction)
    // T2 ---(startTransaction)----------------------------(addPopulation)---(saveInDB[2100] | endTransaction)
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

    // T1 ---(startTransaction)---(readPopulation[2000])--------------------------------------------------------(readPopulationFromCache[2000] | addPopulation)---(saveInDB[2100] | endTransaction)
    // T2 ---(startTransaction)----------------------------(addPopulation)---(saveInDB[2100] | endTransaction)
    @Test
    @DisplayName("TransactionReadCommited(read population) -> SecondTransaction(add population) -> TransactionReadCommited(read and add population)")
    // default entity manager cache entites in L1 cache (cache by transaction)
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

    // T1 ---(startTransaction)---(readPopulation[2000])--------------------------------------------------------(cleanCacheAndReadPopulation[21000] | addPopulation)---(saveInDB[2200] | endTransaction)
    // T2 ---(startTransaction)----------------------------(addPopulation)---(saveInDB[2100] | endTransaction)
    @Test
    @DisplayName("TransactionReadCommited(read population) -> SecondTransaction(add population) -> TransactionReadCommited(clean cache and read population)")
    void readPopulationT1_addPopulationT2_cleanCacheAndReadPopulationT1() throws ExecutionException, InterruptedException {
        CityEntity city = repository.save(CityFactory.create());

        CompletableFuture<CityEntity> readCommited = CompletableFuture.supplyAsync(() -> service.readPopulation_wait_cleanCacheAndReadPopulation(city.getId()));
        CompletableFuture<CityEntity> addPopulation = CompletableFuture.supplyAsync(() -> service.addPopulation(city.getId()));

        readCommited.get();
        addPopulation.get();

        assertThat(repository.findAll())
                .hasSize(1)
                .element(0)
                .extracting(CityEntity::getPopulation).isEqualTo(2200);
    }
}
