package ru.yandex.practicum;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

/*
в главном классе нам нужно:
    создать лог-файл (он должен передаваться во все классы)
    создать загрузчик словарей WordleDictionaryLoader
    загрузить словарь WordleDictionary с помощью класса WordleDictionaryLoader
    затем создать игру WordleGame и передать ей словарь
    вызвать игровой метод в котором в цикле опрашивать пользователя и передавать информацию в игру
    вывести состояние игры и конечный результат
 */
public class Wordle {


    public static void main(String[] args) throws WordNotFoundInDictionary {
        try (PrintWriter log = new PrintWriter(new FileWriter("wordle.log"))) {

            WordleDictionaryLoader wordleDictionaryLoader = new WordleDictionaryLoader(log);
            WordleDictionary wordleDictionary = wordleDictionaryLoader.dictionaryLoader();
            WordleGame wordleGame = new WordleGame(wordleDictionary, wordleDictionary.getWords(), log);
            Scanner scanner = new Scanner(System.in);


            System.out.println("Добро пожаловать в игру Wordle! У тебя есть 6 попыток, слова должны состоять из 5 букв. Удачной игры!");

            while (!wordleGame.isGameOver()) {
                try {
                    System.out.println("Введите слово");
                    String attempt = scanner.nextLine();
                    if (attempt.isEmpty()) {
                        System.out.println(wordleGame.getHint());
                    } else {
                        wordleGame.makeAttempt(attempt, scanner);
                        if (!wordleGame.isCorrect()) {
                            System.out.println("Неверно! Осталось попыток: " + wordleGame.getSteps());
                            System.out.println(wordleGame.charsHint(attempt));
                        } else {
                            break;
                        }
                    }
                } catch (WordNotFoundInDictionary e) {
                    System.out.println("Ошибка: " + e.getMessage());
                } catch (IllegalArgumentException e) {
                    System.err.println("Ошибка: " + e.getMessage());
                }
            }
            if (wordleGame.isCorrect()) {
                System.out.println("Поздравляем, вы выиграли! Это слово верное! Игра окончена!");
            } else {
                System.out.println("Игра окончена. Вы проиграли. Правильное слово - " + wordleGame.getAnswer());
            }
        } catch (IOException e) {
            System.err.println("Не удалось создать лог " + e.getMessage());
        }
    }
}
