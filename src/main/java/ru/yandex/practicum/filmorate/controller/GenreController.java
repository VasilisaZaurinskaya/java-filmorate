package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@RestController
@Slf4j
public class GenreController {
    private final GenreStorage genreStorage;

    @Autowired
    public GenreController(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    @GetMapping("/genres")
    public List<Genre> getAllGenre() {
        return genreStorage.getAllGenres();
    }

    @GetMapping("/genres/{id}")
    public Genre  getGenreById(@PathVariable Long id) {
        Genre genre = genreStorage.getGenreById(id);
        if (genre == null) throw new NotFoundException("Нет жанра с id = " + id);
        return genre;
    }

}