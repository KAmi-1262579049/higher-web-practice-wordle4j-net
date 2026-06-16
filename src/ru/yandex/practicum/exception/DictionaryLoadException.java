package ru.yandex.practicum.exception;

// Класс DictionaryLoadException для ошибки загрузки словаря из файла
public class DictionaryLoadException extends ProgramException {
    // Конструктор класса для создания исключения с сообщением и причиной
    public DictionaryLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
