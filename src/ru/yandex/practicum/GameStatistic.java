package ru.yandex.practicum;

import java.time.LocalDateTime;

// Класс GameStatistic для передачи и хранения статистики победы игрока
public class GameStatistic {
    // Поле для хранения никнейма игрока
    private String nickname;
    // Поле для хранения количества использованных попыток
    private int steps;
    // Поле для хранения признака использования подсказок
    private boolean usedHints;
    // Поле для хранения даты и времени победы
    private String wonAt;

    // Конструктор класса для Gson
    public GameStatistic() {
    }

    // Конструктор класса для создания статистики победы
    public GameStatistic(String nickname, int steps, boolean usedHints) {
        this.nickname = nickname;
        this.steps = steps;
        this.usedHints = usedHints;
        this.wonAt = LocalDateTime.now().toString();
    }

    // Метод для получения никнейма игрока
    public String getNickname() {
        return nickname;
    }

    // Метод для получения количества попыток
    public int getSteps() {
        return steps;
    }

    // Метод для проверки использования подсказок
    public boolean isUsedHints() {
        return usedHints;
    }

    // Метод для получения даты и времени победы
    public String getWonAt() {
        return wonAt;
    }
}
