package com.sebczu.poc.transaction.city.repository;

import com.sebczu.poc.transaction.city.repository.entity.CityEntity;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CityService {

    private final CityRepository repository;

    @Transactional
    public void throwException() {
        repository.save(CityFactory.create());
        throw new RuntimeException();
    }

    @SneakyThrows
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public CityEntity readCommited(int id) {
        Thread.sleep(2000);

        CityEntity city = repository.getOne(id);
        log.info("readCommited population: " + city.getPopulation());
        city.setPopulation(city.getPopulation() + 100);
        return repository.save(city);
    }

    @Transactional
    public CityEntity addPopulation(int id) {
        CityEntity city = repository.getOne(id);
        log.info("population: " + city.getPopulation());
        city.setPopulation(city.getPopulation() + 1);
        return repository.save(city);
    }

}
