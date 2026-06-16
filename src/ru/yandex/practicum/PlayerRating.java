package ru.yandex.practicum;

// Класс PlayerRating для строки рейтинга игроков
public class PlayerRating implements Comparable<PlayerRating> {
    // Поле для хранения никнейма игрока
    private final String nickname;
    // Поле для хранения количества побед
    private final int wins;
    // Поле для хранения лучшего результата по попыткам
    private final int bestSteps;
    // Поле для хранения признака нахождения игрока в топе
    private final boolean currentPlayer;

    // Конструктор класса для создания строки рейтинга
    public PlayerRating(String nickname, int wins, int bestSteps, boolean currentPlayer) {
        this.nickname = nickname;
        this.wins = wins;
        this.bestSteps = bestSteps;
        this.currentPlayer = currentPlayer;
    }

    // Метод для получения никнейма игрока
    public String getNickname() {
        return nickname;
    }

    // Метод для получения количества побед
    public int getWins() {
        return wins;
    }

    // Метод для получения лучшего результата
    public int getBestSteps() {
        return bestSteps;
    }

    // Метод для проверки текущего игрока
    public boolean isCurrentPlayer() {
        return currentPlayer;
    }

    // Метод для сравнения игроков по победам, попыткам и никнейму
    @Override
    public int compareTo(PlayerRating other) {
        int byWins = Integer.compare(other.wins, wins);
        if (byWins != 0) {
            return byWins;
        }
        int bySteps = Integer.compare(bestSteps, other.bestSteps);
        if (bySteps != 0) {
            return bySteps;
        }
        return nickname.compareToIgnoreCase(other.nickname);
    }
}
