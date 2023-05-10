package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Mpa {
    private Long id;
    private String name;
    private String description;

    public Mpa(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Mpa(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Mpa() {
    }
}