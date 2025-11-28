package ru.yandex.practicum;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/*
этот класс содержит в себе всю рутину по работе с файлами словарей и с кодировками
    ему нужны методы по загрузке списка слов из файла по имени файла
    на выходе должен быть класс WordleDictionary
 */
public class WordleDictionaryLoader {
    private final PrintWriter log;

    public WordleDictionaryLoader(PrintWriter log) {
        this.log = log;
    }
    public WordleDictionary dictionaryLoader() throws IOException {
        List<String> wordList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("words_ru.txt", StandardCharsets.UTF_8))) {
            while (br.ready()) {
                String line = br.readLine();
                if((!line.contains("-")) && (line.length() == 5)) {
                    wordList.add(line.toLowerCase(Locale.ROOT).replace("ё", "е"));
                }
            }
        } catch (IOException exception) {
            System.err.println("Не удалось загрузить словарь " + exception.getMessage());
        }
    return new WordleDictionary(wordList, log);
    }

}




