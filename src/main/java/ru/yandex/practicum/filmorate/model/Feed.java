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
    private Long eventId;
    private Long userId;
    private String eventType;
    private String operation;
    private Long entityId;
    private Long timestamp;

}
