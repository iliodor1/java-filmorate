package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ConflictRequestException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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
        if (users.values().stream()
                .filter(x -> x.getLogin().equalsIgnoreCase(user.getLogin()))
                .anyMatch(x -> x.getEmail().equalsIgnoreCase(user.getEmail()))) {
            log.error("Пользователь '{}' с элетронной почтой '{}' уже существует.",
                    user.getLogin(), user.getEmail());
            throw new ConflictRequestException("This user already exists");
        }
        isValid(user);
        if (user.getId() == null) user.setId(createId());
        users.put(user.getId(), user);
        return user;
    }

    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            log.error("Пользователь '{}' c id '{}' не найден", user.getLogin(), user.getId());
            throw new UserNotFoundException(
                    String.format("Пользователь с id:'%d' не найден.", user.getId()));
        }
        if (isValid(user)) {
            users.put(user.getId(), user);
            log.info("Данные пользователя '{}' обновлены", user.getLogin());
        }
        return user;
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUser(Long id) {
        if (users.get(id) == null) {
            log.error("Пользователь с id '{}' не найден.", id);
            throw new UserNotFoundException(
                    String.format("Пользователь с id:'%d' не найден.", id)
            );
        }
        return users.get(id);
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        User user = getUser(userId);
        User friend = getUser(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        getUser(userId).getFriends().remove(friendId);
        getUser(friendId).getFriends().remove(userId);
    }

    @Override
    public List<User> getFriends(Long id) {
        return getUser(id).getFriends().stream()
                .map(this::getUser)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(Long id, Long otherId) {
        User user = getUser(id);
        User otherUser = getUser(otherId);
        Set<Long> userFriends = user.getFriends();
        Set<Long> otherUserFriends = otherUser.getFriends();
        return userFriends.stream()
                .filter(otherUserFriends::contains)
                .map(this::getUser)
                .collect(Collectors.toList());
    }

    private boolean isValid(User user) throws ValidationException {
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
            return true;
        }
    }
}
