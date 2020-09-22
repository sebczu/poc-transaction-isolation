package com.sebczu.poc.transaction.city.repository.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "city")
@Entity(name = "City")
@ToString
public class CityEntity {

    @Id
    private Integer id;
    private String name;
    private Integer population;

}
