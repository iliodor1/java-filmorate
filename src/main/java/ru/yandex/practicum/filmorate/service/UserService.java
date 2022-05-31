package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage storage) {
        this.userStorage = storage;
    }

    public User addFriend(Long idUser, Long idFriend) {
        List<User> users = userStorage.getAllUsers();
        checkUserContainsInMap(users, idUser);
        checkUserContainsInMap(users, idFriend);
        User user = userStorage.getUser(idUser);
        User friend = userStorage.getUser(idFriend);
        HashSet<Long> userFriends = user.getFriends();
        HashSet<Long> friendFriends = friend.getFriends();
        userFriends.add(idFriend);
        friendFriends.add(idUser);
        return friend;
    }

    public void deleteFriend(Long idUser, Long idFriend) {
        List<User> users = userStorage.getAllUsers();
        checkUserContainsInMap(users, idUser);
        userStorage.getUser(idUser).getFriends().remove(idFriend);
        userStorage.getUser(idFriend).getFriends().remove(idUser);
    }

    public List<User> getFriends(Long id) {
        List<User> users = userStorage.getAllUsers();
        checkUserContainsInMap(users, id);
        return userStorage.getUser(id).getFriends().stream()
                .map(this::getUser)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Long id, Long otherId) {
        List<User> users = userStorage.getAllUsers();
        checkUserContainsInMap(users, id);
        checkUserContainsInMap(users, otherId);
        User user = userStorage.getUser(id);
        User otherUser = userStorage.getUser(otherId);
        Set<Long> userFriends = user.getFriends();
        Set<Long> otherUserFriends = otherUser.getFriends();
        return userFriends.stream()
                .filter(otherUserFriends::contains)
                .map(userStorage::getUser)
                .collect(Collectors.toList());
    }

    private void checkUserContainsInMap(List<User> users, long id) {
        if (!(users.contains(userStorage.getUser(id)))) {
            log.error("Пользователь с id '{}' не найден.", id);
            throw new UserNotFoundException(
                    String.format("Пользователь с id:'%d' не найден.", id)
            );
        }
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
