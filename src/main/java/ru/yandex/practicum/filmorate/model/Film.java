package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class Film {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    private List<Genre> genres = new ArrayList<>();

    private Mpa mpa;
    private Set<Long> usersWhoLiked = new HashSet<>();
    private List<Director> directors;

    public Film(
            Long id, String name, String description,
            LocalDate releaseDate, Integer duration,
            List<Genre> genres, Mpa mpaRatingId,
            List<Director> directors
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.genres = genres;
        this.mpa = mpaRatingId;
        this.directors = directors;
    }

    public Film() {
    }
}
