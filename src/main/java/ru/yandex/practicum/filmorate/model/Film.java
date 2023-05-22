package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Film {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    private List<Genre> genres = new ArrayList<>();

    private Mpa mpa;
    private Set<Long> usersWhoLiked = new HashSet<>();
    private LinkedHashSet<Director> directors = new LinkedHashSet<>();
}
