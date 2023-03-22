package controller;

import exception.ValidateException;
import lombok.extern.slf4j.Slf4j;
import model.Film;
import org.springframework.util.IdGenerator;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import repository.IdGenerator;


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

    private void validateNewFilm(Film film) throws ValidateException {
        if (film.getName() == null)throw new ValidateException("Название фильма пустое");

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
