package ru.yandex.practicum.exception;

// Класс EmptyDictionaryException для ошибки пустого словаря
public class EmptyDictionaryException extends ProgramException {
    // Конструктор класса для создания исключения с сообщением
    public EmptyDictionaryException(String message) {
        super(message);
    }
}
