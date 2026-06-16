package ru.yandex.practicum;

// Класс GuessResult для хранения результата одной попытки игрока
public class GuessResult {
    // Поле для хранения введённого слова
    private final String word;
    // Поле для хранения подсказки по введённому слову
    private final String hint;

    // Конструктор класса для создания результата попытки
    public GuessResult(String word, String hint) {
        this.word = word;
        this.hint = hint;
    }

    // Метод для получения введённого слова
    public String getWord() {
        return word;
    }

    // Метод для получения подсказки
    public String getHint() {
        return hint;
    }
}
