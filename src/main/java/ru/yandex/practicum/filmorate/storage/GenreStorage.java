package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface GenreStorage {

    Genre createGenre(Genre genre);

    User updateGenre(Genre genre);
    List<Genre> getAllGenres();

    Genre getGenreById(Long id);

}