package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.db.DirectorDbStorage;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class DirectorService {
    private final DirectorDbStorage directorStorage;

    @Autowired
    public DirectorService(DirectorDbStorage directorStorage) {
        this.directorStorage = directorStorage;
    }

    public List<Director> findAll() {
        return directorStorage.findAll();
    }

    public Director findById(Long id) {
        if (id < 0) {
            throw new NotFoundException("id не может быть отрицательным");
        }

        return directorStorage.findById(id);
    }

    public Director create(Director director) {
        return directorStorage.create(director);
    }

    public Director update(Director director) {
        return directorStorage.update(director);
    }
}
