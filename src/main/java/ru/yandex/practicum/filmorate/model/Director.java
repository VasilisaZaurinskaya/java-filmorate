package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class Director {
    private Long id;

    @NotBlank
    private String name;

    @JsonCreator
    public Director(
            @JsonProperty("id") Long id,
            @JsonProperty("name") String name
    ) {
        this.id = id;
        this.name = name;
    }
}