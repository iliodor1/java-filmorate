package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConflictRequestException;
import ru.yandex.practicum.filmorate.storage.user.friendship.FriendStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundRequestException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;
    private final FriendStorage friendStorage;

    @Autowired
    public UserService(@Qualifier("dataBase") UserStorage userStorage,
                       @Qualifier("dataBase") FriendStorage friendStorage) {
        this.userStorage = userStorage;
        this.friendStorage = friendStorage;
    }

    public void addFriend(Long userId, Long friendId) {
        getUser(userId);
        getUser(friendId);
        friendStorage.addFriend(userId, friendId);
    }

    public void deleteFriend(Long userId, Long friendId) {
        getUser(userId);
        getUser(friendId);
        friendStorage.deleteFriend(userId, friendId);
    }

    public List<User> getFriends(Long id) {
        return friendStorage.getFriends(id);
    }

    public List<User> getCommonFriends(Long id, Long otherId) {
        return friendStorage.getCommonFriends(id, otherId);
    }

    public User addUser(User user) {
        throwIfNotValid(user);
        if (getUsers().stream()
                .filter(x -> x.getLogin().equalsIgnoreCase(user.getLogin()))
                .anyMatch(x -> x.getEmail().equalsIgnoreCase(user.getEmail()))) {
            log.error("Пользователь '{}' с элетронной почтой '{}' уже существует.",
                    user.getLogin(), user.getEmail());
            throw new ConflictRequestException("This user already exists");
        }
        return userStorage.add(user);
    }

    public User updateUser(User user) {
        throwIfNotValid(user);
        getUser(user.getId());
        userStorage.update(user);
        log.info("Данные пользователя '{}' обновлены", user.getLogin());
        return user;
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUser(Long id) {
        return userStorage.getUser(id).orElseThrow(() -> new NotFoundRequestException(
                String.format("User with id '%s' does not exist", id))
        );
    }

    private void throwIfNotValid(User user) throws ValidationException {
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.error("Некорректный адрес электронной почты");
            throw new ValidationException("invalid email");
        } else if (user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            log.error("Логин не должен быть пустым и не должен содержать пробелов");
            throw new ValidationException("invalid login");
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Дата рождения не может быть в будущем");
            throw new ValidationException("invalid birthday");
        } else {
            if (user.getName().isBlank()) user.setName(user.getLogin());
        }
    }

}
