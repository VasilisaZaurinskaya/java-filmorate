package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {


    String RECOMMENDED_FILMS = "SELECT f.film_id, f.name AS film_name, f.description AS film_description, " +
            "f.release_date, f.duration, f.mpa_rating_id, m.name AS rating_name, " + "GROUP_CONCAT(g.name SEPARATOR ', ') AS genres\n" +
            "FROM likes l\n" + "JOIN films f ON l.film_id = f.film_id\n" +
            "JOIN mpa_rating m ON f.mpa_rating_id = m.mpa_rating_id\n" + "LEFT JOIN genre_film gf ON f.film_id = gf.film_id\n" +
            "LEFT JOIN genres g ON gf.genre_id = g.genre_id\n" + "WHERE l.film_id NOT IN (SELECT film_id FROM likes WHERE user_id = ?)\n" +
            "  AND l.user_id IN (SELECT user_id \n" + "                    FROM likes \n" +
            "                    WHERE film_id IN (SELECT film_id FROM likes WHERE user_id = ?) \n" + "                      AND user_id != ? \n" +
            "                    GROUP BY user_id \n" + "                    ORDER BY COUNT(film_id) DESC \n" +
            "                    LIMIT 1)\n" + "GROUP BY f.film_id;";

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
