package com.sebczu.poc.transaction.city.repository.factory;

import com.sebczu.poc.transaction.city.repository.entity.CityEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CityFactory {

    public static CityEntity create() {
        return create("Cracow");
    }

    public static CityEntity create(String name) {
        return new CityEntity(1, name, 2000);
    }
}
