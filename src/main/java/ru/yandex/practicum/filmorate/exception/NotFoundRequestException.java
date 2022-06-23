package ru.yandex.practicum.filmorate.exception;

public class NotFoundRequestException extends RuntimeException {
    public NotFoundRequestException(String message) {
        super(message);
    }

}
