package com.sebczu.poc.transaction.city.repository;

import com.sebczu.poc.transaction.city.repository.entity.CityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CityRepository extends JpaRepository<CityEntity, Integer> {

    List<CityEntity> findAllByName(String name);

    @Modifying
    @Query("update City c set c.population = 0 where c.name = :name")
    int resetPopulation(@Param("name") String name);
}
