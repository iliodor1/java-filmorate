package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private Long id;
    private final String email;
    private final String login;
    private String name;
    private final LocalDate birthday;
    private Set<Long> friends = new HashSet<>();
}
