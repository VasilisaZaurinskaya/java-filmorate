package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.db.DirectorDbStorage;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class DirectorService {
    private final DirectorDbStorage directorStorage;



    public List<Director> findAll() {
        return directorStorage.findAll();
    }

    public Director findById(Long id) {
        if (id < 0) {
            log.error("id не может быть отрицательным");
            throw new NotFoundException("");
        }

        return directorStorage.findById(id);
    }


    public Director create(Director director) {
        return directorStorage.create(director);
    }

    public Director update(Director director) {
        return directorStorage.update(director);
    }

    public String delete(Long id) {
        return directorStorage.delete(id);
    }
}
