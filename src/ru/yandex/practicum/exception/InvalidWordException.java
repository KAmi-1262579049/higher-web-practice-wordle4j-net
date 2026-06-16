package ru.yandex.practicum.exception;

// Класс InvalidWordException для ошибки некорректного слова игрока
public class InvalidWordException extends GameException {
    // Конструктор класса для создания исключения с сообщением
    public InvalidWordException(String message) {
        super(message);
    }
}
