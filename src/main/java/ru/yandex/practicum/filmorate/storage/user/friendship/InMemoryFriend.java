package ru.yandex.practicum.filmorate.storage.user.friendship;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Qualifier("memory")
public class InMemoryFriend implements FriendStorage {

    private final UserService userService;

    public InMemoryFriend(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        User user = userService.getUser(userId);
        User friend = userService.getUser(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        userService.getUser(userId).getFriends().remove(friendId);
        userService.getUser(friendId).getFriends().remove(userId);
    }

    @Override
    public List<User> getFriends(Long id) {
        return userService.getUser(id).getFriends().stream()
                .map(userService::getUser)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(Long id, Long otherId) {
        User user = userService.getUser(id);
        User otherUser = userService.getUser(otherId);
        Set<Long> userFriends = user.getFriends();
        Set<Long> otherUserFriends = otherUser.getFriends();
        return userFriends.stream()
                .filter(otherUserFriends::contains)
                .map(userService::getUser)
                .collect(Collectors.toList());
    }

}
