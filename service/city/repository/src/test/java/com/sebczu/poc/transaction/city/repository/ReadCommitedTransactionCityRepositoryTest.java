package com.sebczu.poc.transaction.city.repository;

import com.sebczu.poc.transaction.city.repository.entity.CityEntity;
import com.sebczu.poc.transaction.city.repository.factory.CityFactory;
import com.sebczu.poc.transaction.city.repository.service.ReadCommitedCityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class ReadCommitedTransactionCityRepositoryTest extends CityRepositoryTest {

    @Autowired
    private ReadCommitedCityService service;

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

    // T1 ---(startTransaction)---(readPopulation[0])---------------------------------------------------------(addPopulation)---(saveInDB[1] | endTransaction)
    // T2 ---(startTransaction)----------------------------(addPopulation)---(saveInDB[1] | endTransaction)
    @Test
    void readPopulationT1_addPopulationT2_addPopulationT1() {
        CityEntity city = repository.save(CityFactory.create());

        service.readPopulation_wait_addPopulation(city.getId());

        assertThat(repository.findAll())
                .hasSize(1)
                .element(0)
                .extracting(CityEntity::getPopulation).isEqualTo(1);
    }

    // T1 ---(startTransaction)---(readPopulation[0])--------------------------------------------------------(readPopulationFromCache[0] | addPopulation)---(saveInDB[1] | endTransaction)
    // T2 ---(startTransaction)----------------------------(addPopulation)---(saveInDB[1] | endTransaction)
    @Test
    // default entity manager cache entites in L1 cache (cache by transaction)
    void readPopulationT1_addPopulationT2_readAndAddPopulationT1() {
        CityEntity city = repository.save(CityFactory.create());

        service.readPopulation_wait_readAndAddPopulation(city.getId());

        assertThat(repository.findAll())
                .hasSize(1)
                .element(0)
                .extracting(CityEntity::getPopulation).isEqualTo(1);
    }

    // T1 ---(startTransaction)---(readPopulation[0])--------------------------------------------------------(cleanCacheAndReadPopulation[1] | addPopulation)---(saveInDB[2] | endTransaction)
    // T2 ---(startTransaction)----------------------------(addPopulation)---(saveInDB[1] | endTransaction)
    @Test
    void readPopulationT1_addPopulationT2_cleanCacheAndReadPopulationT1() {
        CityEntity city = repository.save(CityFactory.create());

        service.readPopulation_wait_cleanCacheAndReadPopulation(city.getId());

        assertThat(repository.findAll())
                .hasSize(1)
                .element(0)
                .extracting(CityEntity::getPopulation).isEqualTo(2);
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

    // T1 ---(startTransaction)---(readCities[1])-----------------------------------------------(readCities[2])---(endTransaction)
    // T2 ---(startTransaction)---------------------(addCity)---(saveInDB[2] | endTransaction)
    @Test
    void readCitiesT1_addCityT2_readCitiesT1() {
        repository.save(CityFactory.create());

        int size = service.readCities_wait_readCities();

        assertThat(size).isEqualTo(2);
        assertThat(repository.findAll())
                .hasSize(2);
    }

    // T1 ---(startTransaction)---(readCities[1])-----------------------------------------------(updateCities[2])---(endTransaction)
    // T2 ---(startTransaction)---------------------(addCity)---(saveInDB[2] | endTransaction)
    @Test
    void readCitiesT1_addCityT2_updateCitiesT1() {
        repository.save(CityFactory.create());

        int size = service.readCities_wait_updateCities();

        assertThat(size).isEqualTo(2);
        assertThat(repository.findAll())
                .hasSize(2)
                .extracting(CityEntity::getPopulation)
                .containsExactlyInAnyOrder(0, 0);
    }

    // T1 ---(startTransaction)---(readCities[1])--------------------------------------------------(updateCities[0])---(endTransaction)
    // T2 ---(startTransaction)---------------------(removeCity)---(saveInDB[0] | endTransaction)
    @Test
    void readCitiesT1_removeCityT2_updateCitiesT1() {
        repository.save(CityFactory.create());

        int size = service.readCities_remove_updateCities();

        assertThat(size).isEqualTo(0);
        assertThat(repository.findAll())
                .hasSize(0);
    }
}
