package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundRequestException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
@Slf4j
@Qualifier("memory")
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users;
    private long idCounter;

    public InMemoryUserStorage() {
        users = new HashMap<>();
    }

    private long createId() {
        return ++idCounter;
    }

    @Override
    public User add(User user) {
        if (user.getId() == null) user.setId(createId());
        users.put(user.getId(), user);
        return user;
    }

    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> getUser(Long id) {
        return Optional.of(users.get(id));
    }

}
