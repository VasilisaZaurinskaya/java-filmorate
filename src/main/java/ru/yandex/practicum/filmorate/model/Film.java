package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import org.springframework.data.relational.core.sql.In;

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

    private Long mpaRatingId;
    private Set<Long> usersWhoLiked = new HashSet<>();

    public Film(Long id, String name, String description, LocalDate releaseDate, Integer duration,
                String genre, Long mpaRatingId, Set<Long> usersWhoLiked) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.genre = genre;
        this.mpaRatingId = mpaRatingId;
        this.usersWhoLiked = usersWhoLiked;
    }

    public Film() {
    }
}
