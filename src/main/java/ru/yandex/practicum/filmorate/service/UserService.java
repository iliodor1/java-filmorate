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

    public User addFriend(Long idUser, Long idFriend) {
        User user = userStorage.getUser(idUser);
        User friend = userStorage.getUser(idFriend);
        user.getFriends().add(idFriend);
        friend.getFriends().add(idUser);
        return friend;
    }

    public void deleteFriend(Long idUser, Long idFriend) {
        userStorage.getUser(idUser).getFriends().remove(idFriend);
        userStorage.getUser(idFriend).getFriends().remove(idUser);
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

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUser(Long id) {
        return userStorage.getUser(id);
    }
}
