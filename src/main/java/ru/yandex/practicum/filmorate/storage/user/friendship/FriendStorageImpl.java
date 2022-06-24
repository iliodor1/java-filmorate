package ru.yandex.practicum.filmorate.storage.user.friendship;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mapper.UserRowMapper;

import java.util.List;

@Component
@Qualifier("dataBase")
public class FriendStorageImpl implements FriendStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FriendStorageImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void addFriend(Long userId, Long friendId) {
        String sqlQuery = "INSERT INTO friends(user_id, friend_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

    public List<User> getFriends(Long id) {
        String sqlQuery = "SELECT * FROM users WHERE id in(SELECT friend_id FROM friends WHERE user_id = ?)";
        return jdbcTemplate.query(sqlQuery, new UserRowMapper(), id);
    }

    @Override
    public List<User> getCommonFriends(Long id, Long otherId) {
        String sqlQuery = "SELECT *\n" +
                "FROM users\n" +
                "WHERE ID =\n" +
                "    (SELECT friend_id\n" +
                "     FROM friends\n" +
                "     WHERE user_id = ?\n" +
                "       AND friend_id IN\n" +
                "         (SELECT friend_id\n" +
                "          FROM friends\n" +
                "          WHERE user_id = ?));";

        return jdbcTemplate.query(sqlQuery, new UserRowMapper(), id, otherId);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        String sqlQuery = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

}
