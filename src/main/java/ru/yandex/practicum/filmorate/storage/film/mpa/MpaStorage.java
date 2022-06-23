package ru.yandex.practicum.filmorate.storage.film.mpa;

import org.apache.catalina.LifecycleState;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

public interface MpaStorage {
    Optional<Mpa> get(int id);
    List<Mpa> getAll();
}
