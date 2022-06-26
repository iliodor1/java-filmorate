package ru.yandex.practicum.filmorate.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundRequestException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.mpa.MpaStorage;

import java.util.List;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class MpaService {

    MpaStorage mpaStorage;

    public Mpa getMpa(int id) {
        return mpaStorage.get(id).orElseThrow(() -> new NotFoundRequestException(
                        String.format("mpa with id '%s' not exist", id)
                )
        );
    }

    public List<Mpa> getMpas() {
        return mpaStorage.getAll();
    }

}
