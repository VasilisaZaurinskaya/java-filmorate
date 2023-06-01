package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {


    String RECOMMENDED_FILMS = "SELECT f.film_id, f.name AS film_name, f.description AS film_description, " +
            "f.release_date, f.duration, f.mpa_rating_id, m.name AS rating_name," + "GROUP_CONCAT(g.name SEPARATOR ', ') AS genres " +
            "FROM likes l " +
            "JOIN films f ON l.film_id = f.film_id JOIN mpa_rating m ON f.mpa_rating_id = m.mpa_rating_id " +
            "LEFT JOIN genre_film gf ON f.film_id = gf.film_id LEFT JOIN genres g ON gf.genre_id = g.genre_id WHERE l.film_id NOT IN (SELECT film_id " +
            "FROM likes WHERE user_id = ?) " +
            "AND l.user_id IN (SELECT user_id FROM likes WHERE film_id IN (SELECT film_id FROM likes WHERE user_id = ?) " + "AND user_id != ?" +
            "GROUP BY user_id " + "ORDER BY COUNT(film_id) DESC LIMIT 1) " + "GROUP BY f.film_id;";

    Film createFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getAllFilms();

    Film getFilmById(Long filmId);

    void addLike(Long filmId, Long userId);

    List<Film> getMostPopularFilms(Integer count, Integer genreId, Integer year);

    List<Film> getCommonFilms(Integer userId, Integer friendId);

    void removeLike(Long filmId, Long userId);

    List<Film> findFilmsByDirector(Long id, Optional<String> sortBy);


    List<Film> getRecommendations(Integer userId);

    List<Film> searchBy(String query, String by);

    void deleteFilm(Long id);
}
