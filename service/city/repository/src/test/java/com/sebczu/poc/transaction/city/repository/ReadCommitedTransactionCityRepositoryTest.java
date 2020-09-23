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

    //[X] - actual value in transaction

    // T1 ---(startTransaction)--------------------------------------------------------(readPopulation[1] | addPopulation)---(saveInDB[2] | endTransaction)
    // T2 ---(startTransaction)---(addPopulation)---(saveInDB[1] | endTransaction)
    @Test
    @DisplayName("TransactionReadCommited(start transaction) -> SecondTransaction(add population) -> TransactionReadCommited(read commited population)")
    void startTransactionT1_addPopulationT2_readPopulationT1() throws ExecutionException, InterruptedException {
        CityEntity city = repository.save(CityFactory.create());

        CompletableFuture<CityEntity> readCommited = CompletableFuture.supplyAsync(() -> service.wait_readAndAddPopulation(city.getId()));
        readCommited.get();

        assertThat(repository.findAll())
                .hasSize(1)
                .element(0)
                .extracting(CityEntity::getPopulation).isEqualTo(2);
    }

    // T1 ---(startTransaction)---(readPopulation[0])---------------------------------------------------------(addPopulation)---(saveInDB[1] | endTransaction)
    // T2 ---(startTransaction)----------------------------(addPopulation)---(saveInDB[1] | endTransaction)
    @Test
    @DisplayName("TransactionReadCommited(read population) -> SecondTransaction(add population) -> TransactionReadCommited(add population)")
    void readPopulationT1_addPopulationT2_addPopulationT1() throws ExecutionException, InterruptedException {
        CityEntity city = repository.save(CityFactory.create());

        CompletableFuture<CityEntity> readCommited = CompletableFuture.supplyAsync(() -> service.readPopulation_wait_addPopulation(city.getId()));
        readCommited.get();

        assertThat(repository.findAll())
                .hasSize(1)
                .element(0)
                .extracting(CityEntity::getPopulation).isEqualTo(1);
    }

    // T1 ---(startTransaction)---(readPopulation[0])--------------------------------------------------------(readPopulationFromCache[0] | addPopulation)---(saveInDB[1] | endTransaction)
    // T2 ---(startTransaction)----------------------------(addPopulation)---(saveInDB[1] | endTransaction)
    @Test
    @DisplayName("TransactionReadCommited(read population) -> SecondTransaction(add population) -> TransactionReadCommited(read and add population)")
    // default entity manager cache entites in L1 cache (cache by transaction)
    void readPopulationT1_addPopulationT2_readAndAddPopulationT1() throws ExecutionException, InterruptedException {
        CityEntity city = repository.save(CityFactory.create());

        CompletableFuture<CityEntity> readCommited = CompletableFuture.supplyAsync(() -> service.readPopulation_wait_readAndAddPopulation(city.getId()));
        readCommited.get();

        assertThat(repository.findAll())
                .hasSize(1)
                .element(0)
                .extracting(CityEntity::getPopulation).isEqualTo(1);
    }

    // T1 ---(startTransaction)---(readPopulation[0])--------------------------------------------------------(cleanCacheAndReadPopulation[1] | addPopulation)---(saveInDB[2] | endTransaction)
    // T2 ---(startTransaction)----------------------------(addPopulation)---(saveInDB[1] | endTransaction)
    @Test
    @DisplayName("TransactionReadCommited(read population) -> SecondTransaction(add population) -> TransactionReadCommited(clean cache and read population)")
    void readPopulationT1_addPopulationT2_cleanCacheAndReadPopulationT1() throws ExecutionException, InterruptedException {
        CityEntity city = repository.save(CityFactory.create());

        CompletableFuture<CityEntity> readCommited = CompletableFuture.supplyAsync(() -> service.readPopulation_wait_cleanCacheAndReadPopulation(city.getId()));
        readCommited.get();

        assertThat(repository.findAll())
                .hasSize(1)
                .element(0)
                .extracting(CityEntity::getPopulation).isEqualTo(2);
    }

    // T1 ---(startTransaction)------------------------------------------------(readCities[2] | endTransaction)
    // T2 ---(startTransaction)---(addCity)---(saveInDB[2] | endTransaction)
    @Test
    @DisplayName("TransactionReadCommited(start transaction) -> SecondTransaction(add city) -> TransactionReadCommited(read commited city)")
    void startTransactionT1_addCityT2_readCitiesT1() throws ExecutionException, InterruptedException {
        repository.save(CityFactory.create());

        CompletableFuture<Integer> readCommited = CompletableFuture.supplyAsync(() -> service.wait_readCities());
        int size = readCommited.get();

        assertThat(size).isEqualTo(2);
        assertThat(repository.findAll())
                .hasSize(2);
    }

    // T1 ---(startTransaction)---(readCities[1])-----------------------------------------------(readCities[2])---(endTransaction)
    // T2 ---(startTransaction)---------------------(addCity)---(saveInDB[2] | endTransaction)
    @Test
    @DisplayName("TransactionReadCommited(start transaction) -> SecondTransaction(add city) -> TransactionReadCommited(read commited city)")
    void readCitiesT1_addCityT2_readCitiesT1() throws ExecutionException, InterruptedException {
        repository.save(CityFactory.create());

        CompletableFuture<Integer> readCommited = CompletableFuture.supplyAsync(() -> service.readCities_wait_readCities());
        int size = readCommited.get();

        assertThat(size).isEqualTo(2);
        assertThat(repository.findAll())
                .hasSize(2);
    }
}
