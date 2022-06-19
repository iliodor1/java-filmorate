package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    User add(User user);

    User update(User user);

    List<User> getUsers();

    User getUser(Long id);

    void addFriend(Long userId, Long friendId);

    List<User> getFriends(Long id);

    List<User> getCommonFriends(Long id, Long otherId);

    void deleteFriend(Long userId, Long friendId);

}
