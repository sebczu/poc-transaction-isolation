package com.sebczu.poc.transaction.city.repository.service;

import com.sebczu.poc.transaction.city.repository.CityRepository;
import com.sebczu.poc.transaction.city.repository.entity.CityEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static com.sebczu.poc.transaction.city.repository.service.BasicCityService.POPULATION_TO_ADD;

@Slf4j
@Service
@RequiredArgsConstructor
public class SerializableCityService {

    private final CityRepository repository;
    private final EntityManager entityManager;
    private final ModifierCityService modifierService;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public CityEntity wait_readAndAddPopulation(int id) {
        modifierService.addPopulation(id);

        CityEntity city = repository.getOne(id);
        log.info("serializable population: " + city.getPopulation());
        city.setPopulation(city.getPopulation() + POPULATION_TO_ADD);
        return repository.save(city);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public CityEntity readPopulation_wait_addPopulation(int id) {
        CityEntity city = repository.getOne(id);
        Integer population = city.getPopulation();
        log.info("serializable population: " + population);

        modifierService.addPopulation(id);

        city.setPopulation(population + POPULATION_TO_ADD);
        return repository.save(city);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public CityEntity readPopulation_wait_readAndAddPopulation(int id) {
        CityEntity city = repository.getOne(id);
        log.info("serializable population: " + city.getPopulation());

        modifierService.addPopulation(id);

        city = repository.getOne(id);
        log.info("serializable population: " + city.getPopulation());
        city.setPopulation(city.getPopulation() + POPULATION_TO_ADD);
        return repository.save(city);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public CityEntity readPopulation_wait_cleanCacheAndReadPopulation(int id) {
        CityEntity city = repository.getOne(id);
        log.info("serializable population: " + city.getPopulation());

        modifierService.addPopulation(id);

        entityManager.clear();
        city = repository.getOne(id);
        log.info("serializable population: " + city.getPopulation());
        city.setPopulation(city.getPopulation() + POPULATION_TO_ADD);
        return repository.save(city);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Integer wait_readCities() {
        modifierService.addCity();

        int size = repository.findAllByName("Cracow").size();
        log.info("serializable cities: " + size);
        return size;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Integer readCities_wait_readCities() {
        int size = repository.findAllByName("Cracow").size();
        log.info("serializable cities: " + size);

        modifierService.addCity();

        size = repository.findAllByName("Cracow").size();
        log.info("serializable cities: " + size);
        return size;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Integer readCities_wait_updateCities() {
        int size = repository.findAllByName("Cracow").size();
        log.info("serializable cities: " + size);

        modifierService.addCity();

        size = repository.resetPopulation("Cracow");
        log.info("serializable cities: " + size);
        return size;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Integer readCities_remove_updateCities() {
        int size = repository.findAllByName("Cracow").size();
        log.info("serializable cities: " + size);

        modifierService.removeCities();

        size = repository.resetPopulation("Cracow");
        log.info("serializable cities: " + size);
        return size;
    }
}
