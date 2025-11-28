package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


class WordleGameTest {

    private WordleGame game;
    private WordleDictionary dictionary;
    private PrintWriter log;

    @BeforeEach
    void setUp() throws WordNotFoundInDictionary, IOException {
        log = new PrintWriter(System.out);
        List<String> testWords = Arrays.asList("ночка", "речка", "столы", "лесок", "парта", "дверь", "лампа");
        dictionary = new WordleDictionary(testWords, log);
        game = new WordleGame(dictionary, testWords, log);
    }

    @Test
    void shouldCreateGame() {
        assertNotNull(game);
        assertNotNull(game.getAnswer());
        assertEquals(5, game.getAnswer().length());
        assertEquals(6, game.getSteps());
        assertFalse(game.isCorrect());
        assertFalse(game.isGameOver());
    }

    @Test
    void shouldDecreaseStepsAfterAttempt() throws WordNotFoundInDictionary {
        Scanner scanner = new Scanner(new ByteArrayInputStream("парта\n".getBytes()));
        String attempt = "парта";

        int initialSteps = game.getSteps();
        game.makeAttempt(attempt, scanner);

        assertEquals(initialSteps - 1, game.getSteps());
    }

    @Test
    void shouldThrowExceptionForWrongLengthWord() {
        Scanner scanner = new Scanner(new ByteArrayInputStream("дом\n".getBytes()));
        String attempt = "дом";

        WordNotFoundInDictionary exception = assertThrows(WordNotFoundInDictionary.class,
                () -> game.makeAttempt(attempt, scanner));
        assertEquals("В слове должно быть 5 букв", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForWordNotInDictionary() {
        Scanner scanner = new Scanner(new ByteArrayInputStream("абвгд\n".getBytes()));
        String attempt = "абвгд";

        WordNotFoundInDictionary exception = assertThrows(WordNotFoundInDictionary.class,
                () -> game.makeAttempt(attempt, scanner));
        assertEquals("Такого слова в словаре нет", exception.getMessage());
    }

    @Test
    void shouldDetectWinSituation() throws WordNotFoundInDictionary {
        String correctAnswer = game.getAnswer();
        Scanner scanner = new Scanner(new ByteArrayInputStream(correctAnswer.getBytes()));

        game.makeAttempt(correctAnswer, scanner);

        assertTrue(game.isCorrect());
        assertTrue(game.isGameOver());
    }

    @Test
    void shouldDetectLoseSituation() throws WordNotFoundInDictionary {
        for (int i = 0; i < 6; i++) {
            Scanner scanner = new Scanner(new ByteArrayInputStream("ночка\n".getBytes()));
            game.makeAttempt("ночка", scanner);
        }

        assertEquals(0, game.getSteps());
        assertTrue(game.isGameOver());
    }

    @Test
    void shouldReturnCorrectCharHints() throws IOException, WordNotFoundInDictionary {
        String answer = "ночка";
        WordleGame testGame = new WordleGame(dictionary, dictionary.getWords(), log) {
            {
                this.setAnswer("ночка");
            }

            @Override
            public void fillAnswer() {
            }
        };

        String hint = testGame.charsHint("навык");
        assertEquals("+^--^", hint);
    }

    @Test
    void shouldGenerateHint() throws WordNotFoundInDictionary {
        Scanner scanner1 = new Scanner(new ByteArrayInputStream("ночка\n".getBytes()));
        game.makeAttempt("ночка", scanner1);

        String hint = game.getHint();
        assertNotNull(hint);
        assertEquals(5, hint.length());
        assertTrue(dictionary.getWords().contains(hint));
    }
}