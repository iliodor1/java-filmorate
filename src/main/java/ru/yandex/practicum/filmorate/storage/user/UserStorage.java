package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {

    User add(User user);

    User update(User user);

    List<User> getUsers();

    Optional<User> getUser(Long id);

}
