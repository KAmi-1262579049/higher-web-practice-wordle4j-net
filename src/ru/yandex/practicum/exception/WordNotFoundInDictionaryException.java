package ru.yandex.practicum.exception;

// Класс WordNotFoundInDictionaryException для ошибки отсутствия слова в словаре
public class WordNotFoundInDictionaryException extends GameException {
    // Конструктор класса для создания исключения с сообщением
    public WordNotFoundInDictionaryException(String message) {
        super(message);
    }
}
