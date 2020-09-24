package com.sebczu.poc.transaction.city.repository;

import com.sebczu.poc.transaction.city.repository.entity.CityEntity;
import com.sebczu.poc.transaction.city.repository.factory.CityFactory;
import com.sebczu.poc.transaction.city.repository.service.RepeatableReadCityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.CannotAcquireLockException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class RepeatableReadTransactionCityRepositoryTest extends CityRepositoryTest {

    @Autowired
    private RepeatableReadCityService service;

    //[X] - actual value in transaction

    // T1 ---(startTransaction)--------------------------------------------------------(readPopulation[1] | addPopulation)---(saveInDB[2] | endTransaction)
    // T2 ---(startTransaction)---(addPopulation)---(saveInDB[1] | endTransaction)
    @Test
    void startTransactionT1_addPopulationT2_readPopulationT1() {
        CityEntity city = repository.save(CityFactory.create());

        service.wait_readAndAddPopulation(city.getId());

        assertThat(repository.findAll())
                .hasSize(1)
                .element(0)
                .extracting(CityEntity::getPopulation).isEqualTo(2);
    }

    // T1 ---(startTransaction)---(readPopulation[0])---------------------------------------------------------(addPopulation)---(rollbackTransaction)
    // T2 ---(startTransaction)----------------------------(addPopulation)---(saveInDB[1] | endTransaction)
    @Test
    void readPopulationT1_addPopulationT2_addPopulationT1() {
        CityEntity city = repository.save(CityFactory.create());

        Throwable thrown = catchThrowable(() -> service.readPopulation_wait_addPopulation(city.getId()));

        assertThat(thrown)
                .isInstanceOf(CannotAcquireLockException.class);

        assertThat(repository.findAll())
                .hasSize(1)
                .element(0)
                .extracting(CityEntity::getPopulation).isEqualTo(1);
    }

    // T1 ---(startTransaction)---(readPopulation[0])--------------------------------------------------------(readPopulationFromCache[0] | addPopulation)---(rollbackTransaction)
    // T2 ---(startTransaction)----------------------------(addPopulation)---(saveInDB[1] | endTransaction)
    @Test
    void readPopulationT1_addPopulationT2_readAndAddPopulationT1() {
        CityEntity city = repository.save(CityFactory.create());

        Throwable thrown = catchThrowable(() -> service.readPopulation_wait_readAndAddPopulation(city.getId()));

        assertThat(thrown)
                .isInstanceOf(CannotAcquireLockException.class);

        assertThat(repository.findAll())
                .hasSize(1)
                .element(0)
                .extracting(CityEntity::getPopulation).isEqualTo(1);
    }

    // T1 ---(startTransaction)---(readPopulation[0])--------------------------------------------------------(cleanCacheAndReadPopulation[1] | addPopulation)---(rollbackTransaction)
    // T2 ---(startTransaction)----------------------------(addPopulation)---(saveInDB[1] | endTransaction)
    @Test
    void readPopulationT1_addPopulationT2_cleanCacheAndReadPopulationT1() {
        CityEntity city = repository.save(CityFactory.create());

        Throwable thrown = catchThrowable(() -> service.readPopulation_wait_cleanCacheAndReadPopulation(city.getId()));

        assertThat(thrown)
                .isInstanceOf(CannotAcquireLockException.class);

        assertThat(repository.findAll())
                .hasSize(1)
                .element(0)
                .extracting(CityEntity::getPopulation).isEqualTo(1);
    }

    // T1 ---(startTransaction)------------------------------------------------(readCities[2] | endTransaction)
    // T2 ---(startTransaction)---(addCity)---(saveInDB[2] | endTransaction)
    @Test
    void startTransactionT1_addCityT2_readCitiesT1() {
        repository.save(CityFactory.create());

        int size = service.wait_readCities();

        assertThat(size).isEqualTo(2);
        assertThat(repository.findAll())
                .hasSize(2);
    }

    // T1 ---(startTransaction)---(readCities[1])-----------------------------------------------(readCities[1])---(endTransaction)
    // T2 ---(startTransaction)---------------------(addCity)---(saveInDB[2] | endTransaction)
    @Test
    void readCitiesT1_addCityT2_readCitiesT1() {
        repository.save(CityFactory.create());

        int size = service.readCities_wait_readCities();

        assertThat(size).isEqualTo(1);
        assertThat(repository.findAll())
                .hasSize(2);
    }
}
