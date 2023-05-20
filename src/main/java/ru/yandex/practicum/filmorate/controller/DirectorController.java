package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/directors")
@Slf4j
@Validated
public class DirectorController {
    private final DirectorService directorService;

    @Autowired
    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @GetMapping
    public ResponseEntity<List<Director>> findAll() {
        return ResponseEntity.ok(directorService.findAll());
    }

    @PostMapping
    public ResponseEntity<Director> create(@Valid @RequestBody Director director) {
        return ResponseEntity.ok(directorService.create(director));
    }

    @PutMapping
    public ResponseEntity<Director> update(@Valid @RequestBody Director director) {
        return ResponseEntity.ok(directorService.update(director));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Director> findRatingById(@PathVariable Long id) {
        return ResponseEntity.ok(directorService.findById(id));
    }
}