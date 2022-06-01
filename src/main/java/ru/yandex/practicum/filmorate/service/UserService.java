package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage storage) {
        this.userStorage = storage;
    }

    public User addFriend(Long userId, Long friendId) {
        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        return friend;
    }

    public void deleteFriend(Long userId, Long friendId) {
        userStorage.getUser(userId).getFriends().remove(friendId);
        userStorage.getUser(friendId).getFriends().remove(userId);
    }

    public List<User> getFriends(Long id) {
        return userStorage.getUser(id).getFriends().stream()
                .map(this::getUser)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Long id, Long otherId) {
        User user = userStorage.getUser(id);
        User otherUser = userStorage.getUser(otherId);
        Set<Long> userFriends = user.getFriends();
        Set<Long> otherUserFriends = otherUser.getFriends();
        return userFriends.stream()
                .filter(otherUserFriends::contains)
                .map(userStorage::getUser)
                .collect(Collectors.toList());
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUser(Long id) {
        return userStorage.getUser(id);
    }
}
