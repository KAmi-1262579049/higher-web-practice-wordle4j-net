package ru.yandex.practicum;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

// Класс WordleServerStatisticLoaderTest для тестирования JSON-статистики игроков
class WordleServerStatisticLoaderTest {
    // Поле для хранения временной папки теста
    @TempDir
    Path tempDir;

    // Тест для проверки сохранения и загрузки статистики
    @Test
    void shouldSaveAndLoadStatistics() throws Exception {
        Path statisticPath = tempDir.resolve("stats.json");
        WordleServerStatisticLoader loader = new WordleServerStatisticLoader(statisticPath, new PrintWriter(System.out));
        loader.addWin(new GameStatistic("kamila", 3, false));
        loader.addWin(new GameStatistic("kamila", 4, true));
        loader.addWin(new GameStatistic("alina", 2, false));
        assertEquals(3, loader.loadAll().size());
        List<PlayerRating> top = loader.getTop("kamila");
        assertEquals("kamila", top.get(0).getNickname());
        assertEquals(2, top.get(0).getWins());
        assertFalse(top.isEmpty());
    }
}
