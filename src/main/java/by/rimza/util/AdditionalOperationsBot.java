package by.rimza.util;

import by.rimza.model.Word;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AdditionalOperationsBot {

    public static String name(Update update) {
        return update.getMessage().getFrom().getUserName();
    }

    public static long userId(Update update) {
        return update.getMessage().getChatId();
    }

    public static String nameByCallBack(Update update) {
        return update.getCallbackQuery().getFrom().getFirstName();
    }

    public static long userIdByCallBack(Update update) {
        return update.getCallbackQuery().getFrom().getId();
    }


    public static String callBack(Update update) {
        return update.getCallbackQuery().getData();
    }


    public static List<Word> word(List<String> list) {
        List<Word> listOfWord = new ArrayList<>();
        for (int i = 1; i <= list.size(); i += 2) {
            Word word = new Word();
            word.setWord(list.get(i - 1).toLowerCase());
            word.setTranslate(list.get(i).toLowerCase());
            listOfWord.add(word);
        }
        return listOfWord;
    }

    public static List<List<String>> divide(Update update) {
        List<List<String>> dividedLists = new ArrayList<>();
        List<String> list = List.of(AdditionalOperationsBot.textFromUpdate(update).split("[-\n]"));
        int size = list.size();
        int index = size / 2;
        String lastWord = list.get(size - 1);
        List<String> list1 = new ArrayList<>();
        List<String> list2 = new ArrayList<>();
        if (CheckLanguage.isEn(list.get(index))) {
            list1 = list.subList(0, index + 2);
            list2 = list.subList(index + 2, size);
        } else {
            list1 = list.subList(0, index + 1);
            list2 = list.subList(index + 1, size);
        }
        dividedLists.add(list1);
        dividedLists.add(list2);
        return dividedLists;

    }

    public static String textFromUpdate(Update update) {
        return update.getMessage().getText().toLowerCase();
    }

    public static int getRandomNum(int max) {
        Random random = new Random();
        return random.nextInt(max);
    }


}
