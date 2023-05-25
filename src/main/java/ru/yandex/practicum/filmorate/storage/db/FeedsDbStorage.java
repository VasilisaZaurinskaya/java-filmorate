package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.storage.FeedsStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@Primary
public class FeedsDbStorage implements FeedsStorage {
    private final JdbcTemplate jdbcTemplate;

    public FeedsDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(Feed feed) {
        Map<String, Object> values = new HashMap<>();
        values.put("user_id", feed.getUserId());
        values.put("entity_id", feed.getEntityId());
        values.put("event_type", feed.getEventType());
        values.put("operation", feed.getOperation());
        values.put("timestamp", feed.getTimestamp());

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("feed")
                .usingGeneratedKeyColumns("event_id");


    }

    @Override
    public List<Feed> getFeedByUserId(Long userId) {
        SqlRowSet feedRows = jdbcTemplate.queryForRowSet("select * from feed where user_id = ?", userId);
        ArrayList<Feed> feeds = new ArrayList<Feed>();
        while (feedRows.next()) {

            Feed feed = new Feed();
            feed.setUserId(feedRows.getLong("user_id"));
            feed.setEntityId(feedRows.getLong("entity_id"));
            feed.setOperation(feedRows.getString("operation"));
            feed.setEventType(feedRows.getString("event_type"));
            feed.setTimestamp(feedRows.getLong("timestamp"));

            feeds.add(feed);
            log.info("Найдена лента событий пользователя: {}", userId);


        }

        return feeds;

    }
}
