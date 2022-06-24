package ru.yandex.practicum.filmorate.storage.user.friendship;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendStorage {

    void addFriend(Long userId, Long friendId);

    List<User> getFriends(Long id);

    List<User> getCommonFriends(Long id, Long otherId);

    void deleteFriend(Long userId, Long friendId);

}
