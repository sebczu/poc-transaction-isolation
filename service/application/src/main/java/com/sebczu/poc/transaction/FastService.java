package com.sebczu.poc.transaction;

import com.sebczu.poc.transaction.city.repository.CityRepository;
import com.sebczu.poc.transaction.city.repository.entity.CityEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FastService {

    private final CityRepository repository;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void start(int id) {
        CityEntity city = repository.getOne(id);
        log.info("start " + city.getPopulation());
        city.setPopulation(city.getPopulation() + 1);
        repository.save(city);
        log.info("start after" + city.getPopulation());

        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("start check again" + repository.getOne(id).getPopulation());
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Integer show() {
        CityEntity city = repository.findAll().stream().findAny().get();
        log.info("show " + city.getPopulation());
        city.setPopulation(city.getPopulation() + 100);
        log.info("show after" + city.getPopulation());
        return city.getPopulation();
    }

}
