package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Feeds {
    private Long feedId;
    private Long userId;
    private Long eventId;
    private Long entityId;
    private String eventType;
    private String operation;
    private Long timestamp;

}
