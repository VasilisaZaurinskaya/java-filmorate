package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Mpa {
    private Integer id;
    private String name;
    private String description;


    public Mpa(Integer id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Mpa() {
    }
}