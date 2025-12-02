package ru.yandex.practicum;

public class WordNotFoundInDictionary extends Exception {
    WordNotFoundInDictionary(String message) {
        super(message);
    }
}
