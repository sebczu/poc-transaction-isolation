package com.sebczu.poc.transaction.city.repository.service;

import com.sebczu.poc.transaction.city.repository.CityRepository;
import com.sebczu.poc.transaction.city.repository.entity.CityEntity;
import com.sebczu.poc.transaction.city.repository.factory.CityFactory;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicCityService {

    public static final int POPULATION_TO_ADD = 1;
    private final CityRepository repository;

    @Transactional
    public void throwException() {
        repository.save(CityFactory.create());
        throw new RuntimeException();
    }

}
