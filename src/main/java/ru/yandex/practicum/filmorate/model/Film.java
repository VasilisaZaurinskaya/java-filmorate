package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    private String genre;
    private String mpa_rating;
    private Set<Long> usersWhoLiked = new HashSet<>();

    public Film(Long id, String name, String description, LocalDate releaseDate, Integer duration,
                String genre, String mpa_rating, Set<Long> usersWhoLiked) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.genre = genre;
        this.mpa_rating = mpa_rating;
        this.usersWhoLiked = usersWhoLiked;
    }

    public Film() {
    }
}
