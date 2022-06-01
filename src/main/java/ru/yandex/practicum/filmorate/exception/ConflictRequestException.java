package ru.yandex.practicum.filmorate.exception;

public class ConflictRequestException extends RuntimeException{
    public ConflictRequestException(String message) {
        super(message);
    }
}