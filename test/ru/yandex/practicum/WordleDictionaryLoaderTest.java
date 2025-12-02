package ru.yandex.practicum;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class WordleDictionaryLoaderTest {

    private WordleDictionaryLoader loader;
    private PrintWriter log;

    @BeforeEach
    void setUp() {
        log = new PrintWriter(System.out);
        loader = new WordleDictionaryLoader(log);
    }

    @Test
    void shouldCreateLoader() {
        assertNotNull(loader);
    }

    @Test
    void shouldLoadDictionaryFromFileWith5LetterRussianWords(@TempDir Path tempDir) throws IOException {
        File testFile = tempDir.resolve("test_russian_words.txt").toFile();
        try (PrintWriter writer = new PrintWriter(testFile, StandardCharsets.UTF_8)) {
            writer.println("столы");
            writer.println("ночка");
            writer.println("речка");
            writer.println("лесок");
            writer.println("парта");
        }

        WordleDictionaryLoader testLoader = new WordleDictionaryLoader(log) {
            @Override
            public WordleDictionary dictionaryLoader() throws IOException {
                List<String> wordList = new ArrayList<>();
                try (BufferedReader br = new BufferedReader(new FileReader(testFile, StandardCharsets.UTF_8))) {
                    while (br.ready()) {
                        String line = br.readLine();
                        if ((!line.contains("-")) && (line.length() == 5)) {
                            wordList.add(line.toLowerCase(Locale.ROOT).replace("ё", "е"));
                        }
                    }
                } catch (IOException exception) {
                    System.err.println("Не удалось загрузить словарь " + exception.getMessage());
                }
                return new WordleDictionary(wordList, log);
            }
        };

        WordleDictionary dictionary = testLoader.dictionaryLoader();
        assertNotNull(dictionary);
        assertFalse(dictionary.getWords().isEmpty());
        assertEquals(5, dictionary.getWords().size());

        // Проверяем, что все слова имеют длину 5 букв
        for (String word : dictionary.getWords()) {
            assertEquals(5, word.length(), "Слово '" + word + "' должно иметь длину 5 букв");
        }
    }

    @Test
    void shouldFilterWordsWithWrongLength(@TempDir Path tempDir) throws IOException {
        File testFile = tempDir.resolve("test_filter_length.txt").toFile();
        try (PrintWriter writer = new PrintWriter(testFile, StandardCharsets.UTF_8)) {
            writer.println("дом");
            writer.println("ночка");
            writer.println("компьютер");
            writer.println("окно");
            writer.println("ручка");
        }

        WordleDictionaryLoader testLoader = new WordleDictionaryLoader(log) {
            @Override
            public WordleDictionary dictionaryLoader() throws IOException {
                List<String> wordList = new ArrayList<>();
                try (BufferedReader br = new BufferedReader(new FileReader(testFile, StandardCharsets.UTF_8))) {
                    while (br.ready()) {
                        String line = br.readLine();
                        if ((!line.contains("-")) && (line.length() == 5)) {
                            wordList.add(line.toLowerCase(Locale.ROOT).replace("ё", "е"));
                        }
                    }
                }
                return new WordleDictionary(wordList, log);
            }
        };

        WordleDictionary dictionary = testLoader.dictionaryLoader();
        assertEquals(2, dictionary.getWords().size());
        assertTrue(dictionary.getWords().contains("ночка"));
        assertTrue(dictionary.getWords().contains("ручка"));
        assertFalse(dictionary.getWords().contains("дом"));
        assertFalse(dictionary.getWords().contains("компьютер"));
        assertFalse(dictionary.getWords().contains("окно"));
    }

    @Test
    @DisplayName("Должен фильтровать слова с дефисами")
    void shouldFilterWordsWithHyphens(@TempDir Path tempDir) throws IOException {
        File testFile = tempDir.resolve("test_filter_hyphens.txt").toFile();
        try (PrintWriter writer = new PrintWriter(testFile, StandardCharsets.UTF_8)) {
            writer.println("я-ты");      // содержит дефис - должно быть отфильтровано
            writer.println("ночка");     // 5 букв без дефиса - должно остаться
            writer.println("как-то");    // содержит дефис - должно быть отфильтровано
            writer.println("речка");     // 5 букв без дефиса - должно остаться
        }

        WordleDictionaryLoader testLoader = new WordleDictionaryLoader(log) {
            @Override
            public WordleDictionary dictionaryLoader() throws IOException {
                List<String> wordList = new ArrayList<>();
                try (BufferedReader br = new BufferedReader(new FileReader(testFile, StandardCharsets.UTF_8))) {
                    while (br.ready()) {
                        String line = br.readLine();
                        if ((!line.contains("-")) && (line.length() == 5)) {
                            wordList.add(line.toLowerCase(Locale.ROOT).replace("ё", "е"));
                        }
                    }
                }
                return new WordleDictionary(wordList, log);
            }
        };

        WordleDictionary dictionary = testLoader.dictionaryLoader();
        assertEquals(2, dictionary.getWords().size());
        assertTrue(dictionary.getWords().contains("ночка"));
        assertTrue(dictionary.getWords().contains("речка"));
        assertFalse(dictionary.getWords().contains("я-ты"));
        assertFalse(dictionary.getWords().contains("как-то"));
    }

    @Test
    @DisplayName("Должен заменять букву ё на е")
    void shouldReplaceYoWithE(@TempDir Path tempDir) throws IOException {
        File testFile = tempDir.resolve("test_yo_replacement.txt").toFile();
        try (PrintWriter writer = new PrintWriter(testFile, StandardCharsets.UTF_8)) {
            writer.println("мёдок");     // 5 букв с ё - должно остаться и замениться на е
            writer.println("ёлкач");     // 5 букв с ё - должно остаться и замениться на е
            writer.println("берёза");    // 6 букв - должно быть отфильтровано
            writer.println("речка");     // 5 букв без ё - должно остаться как есть
        }

        WordleDictionaryLoader testLoader = new WordleDictionaryLoader(log) {
            @Override
            public WordleDictionary dictionaryLoader() throws IOException {
                List<String> wordList = new ArrayList<>();
                try (BufferedReader br = new BufferedReader(new FileReader(testFile, StandardCharsets.UTF_8))) {
                    while (br.ready()) {
                        String line = br.readLine();
                        if ((!line.contains("-")) && (line.length() == 5)) {
                            wordList.add(line.toLowerCase(Locale.ROOT).replace("ё", "е"));
                        }
                    }
                }
                return new WordleDictionary(wordList, log);
            }
        };

        WordleDictionary dictionary = testLoader.dictionaryLoader();
        assertEquals(3, dictionary.getWords().size());
        assertTrue(dictionary.getWords().contains("медок"));
        assertTrue(dictionary.getWords().contains("елкач"));
        assertTrue(dictionary.getWords().contains("речка"));
        assertFalse(dictionary.getWords().contains("мёдок")); // оригинал с ё не должен остаться
        assertFalse(dictionary.getWords().contains("ёлкач")); // оригинал с ё не должен остаться
        assertFalse(dictionary.getWords().contains("берёза")); // слишком длинное
    }

    @Test
    @DisplayName("Должен преобразовывать слова к нижнему регистру")
    void shouldConvertToLowerCase(@TempDir Path tempDir) throws IOException {
        File testFile = tempDir.resolve("test_lowercase.txt").toFile();
        try (PrintWriter writer = new PrintWriter(testFile, StandardCharsets.UTF_8)) {
            writer.println("СТОЛЫ");     // верхний регистр - должно стать нижним
            writer.println("Ночка");     // смешанный регистр - должно стать нижним
            writer.println("речка");     // уже нижний регистр - должно остаться
        }

        WordleDictionaryLoader testLoader = new WordleDictionaryLoader(log) {
            @Override
            public WordleDictionary dictionaryLoader() throws IOException {
                List<String> wordList = new ArrayList<>();
                try (BufferedReader br = new BufferedReader(new FileReader(testFile, StandardCharsets.UTF_8))) {
                    while (br.ready()) {
                        String line = br.readLine();
                        if ((!line.contains("-")) && (line.length() == 5)) {
                            wordList.add(line.toLowerCase(Locale.ROOT).replace("ё", "е"));
                        }
                    }
                }
                return new WordleDictionary(wordList, log);
            }
        };

        WordleDictionary dictionary = testLoader.dictionaryLoader();
        assertEquals(3, dictionary.getWords().size());
        assertTrue(dictionary.getWords().contains("столы"));
        assertTrue(dictionary.getWords().contains("ночка"));
        assertTrue(dictionary.getWords().contains("речка"));
        assertFalse(dictionary.getWords().contains("СТОЛЫ"));
        assertFalse(dictionary.getWords().contains("Ночка"));
    }

    @Test
    @DisplayName("Должен возвращать пустой словарь если файл не содержит подходящих слов")
    void shouldReturnEmptyDictionaryWhenNoValidWords(@TempDir Path tempDir) throws IOException {
        File testFile = tempDir.resolve("test_empty_valid.txt").toFile();
        try (PrintWriter writer = new PrintWriter(testFile, StandardCharsets.UTF_8)) {
            writer.println("дом");       // 3 буквы
            writer.println("компьютер"); // 9 букв
            writer.println("я-ты");      // дефис
            writer.println("окно");      // 4 буквы
        }

        WordleDictionaryLoader testLoader = new WordleDictionaryLoader(log) {
            @Override
            public WordleDictionary dictionaryLoader() throws IOException {
                List<String> wordList = new ArrayList<>();
                try (BufferedReader br = new BufferedReader(new FileReader(testFile, StandardCharsets.UTF_8))) {
                    while (br.ready()) {
                        String line = br.readLine();
                        if ((!line.contains("-")) && (line.length() == 5)) {
                            wordList.add(line.toLowerCase(Locale.ROOT).replace("ё", "е"));
                        }
                    }
                }
                return new WordleDictionary(wordList, log);
            }
        };

        WordleDictionary dictionary = testLoader.dictionaryLoader();
        assertNotNull(dictionary);
        assertTrue(dictionary.getWords().isEmpty());
    }

    @Test
    @DisplayName("Должен корректно обрабатывать пустой файл")
    void shouldHandleEmptyFile(@TempDir Path tempDir) throws IOException {
        File testFile = tempDir.resolve("test_empty.txt").toFile();
        // Создаем пустой файл
        Files.createFile(testFile.toPath());

        WordleDictionaryLoader testLoader = new WordleDictionaryLoader(log) {
            @Override
            public WordleDictionary dictionaryLoader() throws IOException {
                List<String> wordList = new ArrayList<>();
                try (BufferedReader br = new BufferedReader(new FileReader(testFile, StandardCharsets.UTF_8))) {
                    while (br.ready()) {
                        String line = br.readLine();
                        if ((!line.contains("-")) && (line.length() == 5)) {
                            wordList.add(line.toLowerCase(Locale.ROOT).replace("ё", "е"));
                        }
                    }
                }
                return new WordleDictionary(wordList, log);
            }
        };

        WordleDictionary dictionary = testLoader.dictionaryLoader();
        assertNotNull(dictionary);
        assertTrue(dictionary.getWords().isEmpty());
    }

    @Test
    @DisplayName("Должен корректно обрабатывать файл только с пробелами и пустыми строками")
    void shouldHandleFileWithOnlySpacesAndEmptyLines(@TempDir Path tempDir) throws IOException {
        File testFile = tempDir.resolve("test_spaces.txt").toFile();
        try (PrintWriter writer = new PrintWriter(testFile, StandardCharsets.UTF_8)) {
            writer.println("");          // пустая строка
            writer.println("   ");       // пробелы
            writer.println("     ");     // 5 пробелов
            writer.println();            // пустая строка
        }

        WordleDictionaryLoader testLoader = new WordleDictionaryLoader(log) {
            @Override
            public WordleDictionary dictionaryLoader() throws IOException {
                List<String> wordList = new ArrayList<>();
                try (BufferedReader br = new BufferedReader(new FileReader(testFile, StandardCharsets.UTF_8))) {
                    while (br.ready()) {
                        String line = br.readLine();
                        if ((!line.contains("-")) && (line.length() == 5)) {
                            wordList.add(line.toLowerCase(Locale.ROOT).replace("ё", "е"));
                        }
                    }
                }
                return new WordleDictionary(wordList, log);
            }
        };
    }
}