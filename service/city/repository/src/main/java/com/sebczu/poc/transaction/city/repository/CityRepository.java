package com.sebczu.poc.transaction.city.repository;

import com.sebczu.poc.transaction.city.repository.entity.CityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CityRepository extends JpaRepository<CityEntity, Integer> {

    List<CityEntity> findAllByName(String name);

}
