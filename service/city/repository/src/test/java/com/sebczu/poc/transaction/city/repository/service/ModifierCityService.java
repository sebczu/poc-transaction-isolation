package com.sebczu.poc.transaction.city.repository.service;

import com.sebczu.poc.transaction.city.repository.CityRepository;
import com.sebczu.poc.transaction.city.repository.entity.CityEntity;
import com.sebczu.poc.transaction.city.repository.factory.CityFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static com.sebczu.poc.transaction.city.repository.service.BasicCityService.POPULATION_TO_ADD;

@Service
@RequiredArgsConstructor
public class ModifierCityService {

    private final CityRepository repository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CityEntity addPopulation(int id) {
        CityEntity city = repository.getOne(id);
        city.setPopulation(city.getPopulation() + POPULATION_TO_ADD);
        return repository.save(city);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void addCity() {
        repository.save(CityFactory.create(2));
    }

}
