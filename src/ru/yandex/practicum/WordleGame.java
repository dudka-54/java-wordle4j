package ru.yandex.practicum;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/*
в этом классе хранится словарь и состояние игры
    текущий шаг
    всё что пользователь вводил
    правильный ответ

в этом классе нужны методы, которые
    проанализируют совпадение слова с ответом
    предложат слово-подсказку с учётом всего, что вводил пользователь ранее

не забудьте про специальные типы исключений для игровых и неигровых ошибок
 */
public class WordleGame {
    PrintWriter log;

    HashMap<Integer, Character> greenChars = new HashMap<>();
    HashSet<Character> yellowChars = new HashSet<>();
    HashSet<Character> grayChars = new HashSet<>();

    Random random = new Random();

    Scanner scanner = new Scanner(System.in);

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    private String answer;

    public int getSteps() {
        return steps;
    }

    public String getAnswer() {
        return answer;
    }

    private int steps = 6;

    private WordleDictionary dictionary;
    LinkedList<String> usedWords = new LinkedList<>();
    List<String> dictionaryList;

    public boolean isCorrect() {
        return isCorrect;
    }

    private boolean isCorrect = false;

    public boolean isGameOver(){
        return isCorrect || steps == 0;
    }

    public String makeAttempt(String attempt, Scanner scanner) throws WordNotFoundInDictionary {
        List<String> dictionaryList = dictionary.getWords();
        boolean success = false;

        while(!success) {
                if(attempt.isEmpty()){
                    String hint = getHint();
                    System.out.println("Подсказка " + hint);
                    attempt = scanner.nextLine();
                    continue;
                }
                if (attempt.length() != 5) {
                    throw new WordNotFoundInDictionary("В слове должно быть 5 букв");
                }
                if (!dictionaryList.contains(attempt)) {
                    throw new WordNotFoundInDictionary("Такого слова в словаре нет");
                }


                    for (int i = 0; i < attempt.length(); i++) {
                        char attemptChar = attempt.charAt(i);

                        char answerChar = answer.charAt(i);
                        if(attempt.equals(answer)){
                            isCorrect = true;
                            break;
                        }
                        if (attemptChar == answerChar) {
                            greenChars.put(i, attemptChar);
                            yellowChars.remove(attemptChar);

                        } else if (answer.contains(String.valueOf(attemptChar))) {
                            yellowChars.add(attemptChar);
                            isCorrect = false;
                        } else {
                            grayChars.add(attemptChar);
                            isCorrect = false;
                        }
                    }
                    steps = steps - 1;
                    usedWords.add(attempt);
                success = true;

        }
        return attempt;
    }

    public String getHint() {
        List<String> possibleWords = new ArrayList<>();
        for (String word : dictionaryList) {
            if (usedWords.contains(word)) {
                continue;
            }

            boolean isValid = true;

            for (char c : grayChars) {
                if (word.contains(String.valueOf(c))) {
                    isValid = false;
                    break;
                }
            }
            if (!isValid) {
                continue;
            }
            if (!greenChars.isEmpty()) {
                for (Map.Entry<Integer, Character> entry : greenChars.entrySet()) {
                    int index = entry.getKey();
                    char letter = entry.getValue();
                    if (index >= word.length() || word.charAt(index) != letter) {
                        isValid = false;
                        break;
                    }
                }
            }
                if (!isValid) {
                    continue;
                }
                for (char yellowChar : yellowChars) {
                    if (!word.contains(String.valueOf(yellowChar))) {
                        isValid = false;
                        break;
                    }
                }
                if (isValid) {
                    possibleWords.add(word);
                }
        }
        if (possibleWords.isEmpty()) {
            return dictionaryList.get(random.nextInt(dictionaryList.size()));
        } else {
            return possibleWords.get(random.nextInt(possibleWords.size()));
        }
    }

    public WordleGame(WordleDictionary dictionary, List<String> dictionaryList, PrintWriter log) throws IOException, WordNotFoundInDictionary {
        this.dictionary = dictionary;
        this.dictionaryList = dictionary.getWords();
        this.log = log;
        fillAnswer();
    }

    public void fillAnswer() throws IOException {
        try {
            if (dictionaryList.isEmpty()) {
                throw new WordNotFoundInDictionary("Словарь пуст");
            }
            this.answer = dictionaryList.get(random.nextInt(dictionaryList.size()));
        } catch (WordNotFoundInDictionary e){
            System.err.println("Ошибка " + e.getMessage());
        }
    }


    public String charsHint(String attempt) {
        StringBuilder charHint = new StringBuilder();

        for(int i = 0; i < attempt.length(); i++) {
            char attemptChar = attempt.charAt(i);

            if (i < answer.length() && attemptChar == answer.charAt(i)) {
                charHint.append('+');
            } else if (answer.contains(String.valueOf(attemptChar))) {
                charHint.append('^');
            } else {
                charHint.append('-');
            }
        }
        return charHint.toString();
    }



}
