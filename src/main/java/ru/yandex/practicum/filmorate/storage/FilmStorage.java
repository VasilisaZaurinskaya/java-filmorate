package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    String RECOMMENDED_FILMS = "SELECT F.film_id, F.name AS film_name, " +
            "F.description AS film_description, F.release_date, F.duration, F.mpa_rating_id, M.name AS rating_name, " +
            "GROUP_CONCAT(G.name SEPARATOR ', ') AS genres " +
            "FROM likes AS L " +
            "JOIN films AS F ON F.film_id = L.film_id " +
            "JOIN mpa_rating AS M ON F.mpa_rating_id = M.mpa_rating_id " +
            "LEFT JOIN genre_film AS GF ON F.film_id = GF.film_id " +
            "LEFT JOIN genres AS G ON GF.genre_id = G.genre_id " +
            "WHERE L.film_id NOT IN (SELECT film_id FROM likes WHERE user_id = ?) " +
            "  AND L.user_id IN (SELECT user_id " +
            "                    FROM likes " +
            "                    WHERE film_id IN (SELECT film_id FROM likes WHERE user_id = ?) " +
            "                      AND user_id != ? " +
            "                    GROUP BY user_id " +
            "                    ORDER BY COUNT(film_id) DESC " +
            "                    LIMIT 1) " +
            "GROUP BY F.film_id;";

    Film createFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getAllFilms();

    Film getFilmById(Long filmId);

    void addLike(Long filmId, Long userId);

    List<Film> getMostPopularFilms(Integer count);

    void removeLike(Long filmId, Long userId);

    List<Film> findFilmsByDirector(Long id, Optional<String> sortBy);

    List<Film> getRecommendations(Integer userId);

    List<Film> searchBy(String query, String by);
}
