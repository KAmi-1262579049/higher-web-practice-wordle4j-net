package ru.yandex.practicum.exception;

// Класс GameException для игровых проверяемых исключений
public class GameException extends Exception {
    // Конструктор класса для создания исключения с сообщением
    public GameException(String message) {
        super(message);
    }
}
