package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Feed {
    /**
     * primary key
     */
    private Long eventId;
    private Long userId;
    /**
     * одно из значениий LIKE, REVIEW или FRIEND
     */
    private String eventType;
    /**
     * одно из значениий REMOVE, ADD, UPDATE
     */
    private String operation;
    /**
     * идентификатор сущности, с которой произошло событие
     */
    private Long entityId;
    private Long timestamp;

}
