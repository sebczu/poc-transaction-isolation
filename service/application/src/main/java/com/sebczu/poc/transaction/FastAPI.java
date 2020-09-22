package com.sebczu.poc.transaction;

import com.sebczu.poc.transaction.city.repository.CityRepository;
import com.sebczu.poc.transaction.city.repository.entity.CityEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class FastAPI {

    private final CityRepository repository;
    private final FastService service;

    @GetMapping("/start")
    public String start() {
        service.start(1);
        return "end";
    }


    @GetMapping("/test")
    public Integer test() {
        return service.show();
    }


}
