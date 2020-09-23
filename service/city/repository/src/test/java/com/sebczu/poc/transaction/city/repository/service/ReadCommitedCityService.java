package com.sebczu.poc.transaction.city.repository.service;

import com.sebczu.poc.transaction.city.repository.CityRepository;
import com.sebczu.poc.transaction.city.repository.entity.CityEntity;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReadCommitedCityService {

    private static final int POPULATION_TO_ADD = 100;
    private final CityRepository repository;
    private final EntityManager entityManager;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public CityEntity wait_readAndAddPopulation(int id) {
        sleep();

        CityEntity city = repository.getOne(id);
        log.info("readCommited population: " + city.getPopulation());
        city.setPopulation(city.getPopulation() + POPULATION_TO_ADD);
        return repository.save(city);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public CityEntity readPopulation_wait_addPopulation(int id) {
        CityEntity city = repository.getOne(id);
        Integer population = city.getPopulation();
        log.info("readCommited population: " + population);

        sleep();

        city.setPopulation(population + POPULATION_TO_ADD);
        return repository.save(city);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public CityEntity readPopulation_wait_readAndAddPopulation(int id) {
        CityEntity city = repository.getOne(id);
        log.info("readCommited population: " + city.getPopulation());

        sleep();

        city = repository.getOne(id);
        log.info("readCommited population: " + city.getPopulation());
        city.setPopulation(city.getPopulation() + POPULATION_TO_ADD);
        return repository.save(city);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public CityEntity readPopulation_wait_cleanCacheAndReadPopulation(int id) {
        CityEntity city = repository.getOne(id);
        log.info("readCommited population: " + city.getPopulation());

        sleep();

        entityManager.clear();
        city = repository.getOne(id);
        log.info("readCommited population: " + city.getPopulation());
        city.setPopulation(city.getPopulation() + POPULATION_TO_ADD);
        return repository.save(city);
    }

    @Transactional
    public CityEntity addPopulation(int id) {
        CityEntity city = repository.getOne(id);
        city.setPopulation(city.getPopulation() + POPULATION_TO_ADD);
        return repository.save(city);
    }

    @SneakyThrows
    private void sleep() {
        log.info("wait");
        Thread.sleep(2000);
    }
}
