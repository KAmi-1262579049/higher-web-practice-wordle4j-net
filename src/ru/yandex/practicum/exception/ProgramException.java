package ru.yandex.practicum.exception;

// Класс ProgramException для системных ошибок программы
public class ProgramException extends Exception {
    // Конструктор класса для создания исключения с сообщением
    public ProgramException(String message) {
        super(message);
    }

    // Конструктор класса для создания исключения с сообщением и причиной
    public ProgramException(String message, Throwable cause) {
        super(message, cause);
    }
}
