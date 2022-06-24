package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.friendship.FriendStorageImpl;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {
    private final UserDbStorage userStorage;
    private final FriendStorageImpl friendStorageImpl;
    private final FilmDbStorage filmDbStorage;

    @Test
    public void testGetUser() {
        Optional<User> userOptional = userStorage.getUser(1L);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    public void testGetUsers() {
        List<User> users = userStorage.getUsers();
        assertEquals(users.size(), 4);
    }

    @Test
    public void testUpdate() {
        User user = new User(1L,
                "emailUpdate@mail.ru",
                "userUpdate",
                "name update",
                LocalDate.of(2010, 10, 10));
        userStorage.update(user);
        Optional<User> userOptional = userStorage.getUser(1L);
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying((u) -> {
                            assertThat(u).hasFieldOrPropertyWithValue("id", 1L);
                            assertThat(u).hasFieldOrPropertyWithValue("email", "emailUpdate@mail.ru");
                            assertThat(u).hasFieldOrPropertyWithValue("login", "userUpdate");
                            assertThat(u).hasFieldOrPropertyWithValue("name", "name update");
                            assertThat(u).hasFieldOrPropertyWithValue(
                                    "birthday", LocalDate.of(2010, 10, 10)
                            );
                        }
                );
    }

    @Test
    public void testAddUser() {
        User user = new User(-30L,
                "email454@mail.ru",
                "User454",
                "User454",
                LocalDate.of(2010, 10, 10));
        User userAdded = userStorage.add(user);
        long id = userAdded.getId();
        Optional<User> userOptional = userStorage.getUser(id);
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying((u) -> {
                            assertThat(u).hasFieldOrPropertyWithValue("id", id);
                            assertThat(u).hasFieldOrPropertyWithValue("email", "email454@mail.ru");
                            assertThat(u).hasFieldOrPropertyWithValue("login", "User454");
                            assertThat(u).hasFieldOrPropertyWithValue("name", "User454");
                            assertThat(u).hasFieldOrPropertyWithValue(
                                    "birthday", LocalDate.of(2010, 10, 10)
                            );
                        }
                );
    }

    @Test
    public void testAddDeleteAndGetFriends() {
        assertEquals(0, friendStorageImpl.getFriends(1L).size());
        Optional<User> friend = userStorage.getUser(2L);
        friendStorageImpl.addFriend(1L, 2L);
        assertTrue(friendStorageImpl.getFriends(1L).contains(friend.orElseThrow()));
        assertEquals(1, friendStorageImpl.getFriends(1L).size());
        friendStorageImpl.deleteFriend(1L, 2L);
        assertEquals(0, friendStorageImpl.getFriends(1L).size());

    }

    @Test
    public void testGetCommonFriends() {
        friendStorageImpl.addFriend(1L, 2L);
        assertEquals(0, friendStorageImpl.getCommonFriends(2L, 3L).size());
        friendStorageImpl.addFriend(4L, 2L);
        assertEquals(1, friendStorageImpl.getCommonFriends(1L, 4L).size());
        friendStorageImpl.deleteFriend(1L, 2L);
    }

    @Test
    public void testAddFilm() {
        Film film = new Film(-100L,
                "name4",
                "description",
                LocalDate.of(2010, 10, 10),
                100,
                new Mpa(1, null),
                Set.of(new Genre(1, null)));
        assertEquals(2, filmDbStorage.getFilms().size());
        filmDbStorage.add(film);
        assertEquals(3, filmDbStorage.getFilms().size());

    }

    @Test
    public void testGetFilm() {
        Optional<Film> filmOptional = filmDbStorage.getFilm(1L);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying((f) -> {
                            assertThat(f).hasFieldOrPropertyWithValue("id", 1L);
                            assertThat(f).hasFieldOrPropertyWithValue("name", "name1");
                            assertThat(f).hasFieldOrPropertyWithValue("description", "description");
                            assertThat(f).hasFieldOrPropertyWithValue(
                                    "releaseDate", LocalDate.of(1977, 1, 1)
                            );
                            assertThat(f).hasFieldOrPropertyWithValue("duration", 100);
                        }
                );
    }

    @Test
    public void testUpdateFilm() {
        Film film = new Film(2L,
                "nameUpdate",
                "descriptionUpdate",
                LocalDate.now(),
                100,
                new Mpa(1, null),
                Set.of(new Genre(1, null)));
        filmDbStorage.update(film);

        Optional<Film> filmOptional = filmDbStorage.getFilm(2L);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying((f) -> {
                            assertThat(f).hasFieldOrPropertyWithValue("id", 2L);
                            assertThat(f).hasFieldOrPropertyWithValue("name", "nameUpdate");
                            assertThat(f).hasFieldOrPropertyWithValue("description", "descriptionUpdate");
                            assertThat(f).hasFieldOrPropertyWithValue(
                                    "releaseDate", LocalDate.now()
                            );
                            assertThat(f).hasFieldOrPropertyWithValue("duration", 100);
                        }
                );
    }

}
