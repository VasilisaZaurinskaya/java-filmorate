package controller;

import exception.ValidateException;
import lombok.extern.slf4j.Slf4j;
import model.Film;
import org.springframework.util.IdGenerator;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
@RequestMapping("/film")
@Slf4j
public class FilmController {
    protected HashMap<Long, Film> filmStorage = new HashMap<>();
    public IdGenerator idGenerator;

    public FilmController(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @PostMapping
    public @ResponseBody Film create(Film film) throws ValidateException {
        validateNewFilm(film);
        if (film.getId() == null) {
            film.setId(generateNewId());
        } else {
            filmStorage.put(film.getId(), film);
        }
        return film;

    }

  public void validateNewFilm(Film film) throws ValidateException {
        if (film.getName() == null) throw new ValidateException("Название фильма пустое");
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28)))
            throw new ValidateException("Дата релиза не может быть раньше 28 декабря 1895 года");
        if (film.getDescription().length() > 200)
            throw new ValidateException("Описание не должно превышать 200 символов");
        if (film.getDuration().toMillis() > 0)
            throw new ValidateException("Продолжительность фильма должна быть положительной");

    }

    public ArrayList<Film> getAllFilm() {
        return new ArrayList<>(filmStorage.values());
    }

    public long getMaxId() {
        long maxId = 0;
        for (long id : filmStorage.keySet()) {
            if (id > maxId) {
                maxId = id;
            }
        }
        return maxId;
    }

    public long generateNewId() {
        long maxId = getMaxId();
        long newId = maxId + 1;
        return newId;
    }
}
