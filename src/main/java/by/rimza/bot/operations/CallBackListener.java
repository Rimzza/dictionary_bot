package by.rimza.bot.operations;

import by.rimza.dao.DictionaryDAO;
import by.rimza.model.Word;
import by.rimza.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.meta.api.objects.Update;


import java.util.*;


public class CallBackListener extends DictionaryBot {

    private Update update;

    private String callBack;


    public CallBackListener(@Value("${botUsername}") String botUsername, @Value(" ${token}") String botToken, DictionaryDAO dictionaryDAO, String callBack) {
        super(botUsername, botToken, dictionaryDAO);
        this.callBack = callBack;
    }

    public String getCallBack() {
        return callBack;
    }

    public void setCallBack(String callBack) {
        this.callBack = callBack;
    }

    protected void checkCallBack(Update update) {
        this.update = update;
        long chatId = AdditionalOperationsBot.userId(update);
        switch (callBack) {
            case "add_word" -> addWord(chatId);
            case "delete_word" -> deleteWord(chatId);
            case "update_word" -> updateWord(chatId);
            case "test" -> testWord(chatId);
        }
    }

    private void updateWord(long chatId) {
        int idPerson = getDictionaryDAO().getPerson(chatId).getId();
        if (getDictionaryDAO().showWordFromPerson_WordsByPersonId(idPerson) == null) {
            updateWordStage1(update, chatId);
        } else {
            updateWordStage2(chatId);
        }
    }

    private void updateWordStage1(Update update, long chatId) {
        Word word;
        if (CheckLanguage.isEn(AdditionalOperationsBot.textFromUpdate(update))) {
            word = getDictionaryDAO().showWordsByWord(chatId, AdditionalOperationsBot.textFromUpdate(update));
        } else {
            word = getDictionaryDAO().showWordsByTranslate(chatId, AdditionalOperationsBot.textFromUpdate(update));
        }
        int idPerson = getDictionaryDAO().getPerson(chatId).getId();
        int idWords = getDictionaryDAO().getIdWord(idPerson, word);
        getDictionaryDAO().addPerson_words(idPerson, idWords);
        sendMessage(chatId, "На что хотите изменить?");

    }

    private void updateWordStage2(long chatId) {
        String changedWord = AdditionalOperationsBot.textFromUpdate(update);
        int idPerson = getDictionaryDAO().getPerson(chatId).getId();
        Word word1 = getDictionaryDAO().showWordFromPerson_WordsByPersonId(idPerson);
        if (CheckLanguage.isEn(changedWord)) {
            word1.setWord(changedWord);
        } else {
            word1.setTranslate(changedWord);
        }
        getDictionaryDAO().update(idPerson, word1);
        getDictionaryDAO().deletePerson_words(idPerson);
        callBack = null;
    }

    private void addWord(long chatId) {
        List<List<String>> lists = AdditionalOperationsBot.divide(update);
        Runnable runnable1 = new MyThread(getDictionaryDAO(), chatId, lists.get(0));
        Runnable runnable2 = new MyThread(getDictionaryDAO(), chatId, lists.get(1));
        Thread thread1 = new Thread(runnable1);
        Thread thread2 = new Thread(runnable2);
        thread1.start();
        thread2.start();
        callBack = null;
    }

    private void deleteWord(long chatId) {
        if (checkWords(chatId)) {
            getDictionaryDAO().delete(chatId, AdditionalOperationsBot.textFromUpdate(update));
        } else {
            sendMessage(chatId, "Для удаления необходимо иметь хотя бы одно слово в словаре");
        }
        callBack = null;
    }

    private void testWord(long chatId) {

        if (AdditionalOperationsBot.textFromUpdate(update).equalsIgnoreCase("stop")) {
            callBack = null;
            return;
        }
        int idPerson = getDictionaryDAO().getPerson(chatId).getId();
        if (!ListForTestWord.getListTestWord().containsKey(idPerson) ||
                ListForTestWord.getOperations().get(idPerson).equals(Operations.WAITING_FOR_INPUT)) {
            testWordStage1(chatId, idPerson);
        } else {
            testWordStage2(chatId, idPerson);
        }
    }

    private void testWordStage1(long chatId, int idPerson) {
        Map<Integer, List<Word>> listTestWord = ListForTestWord.getListTestWord();

        if (!ListForTestWord.getListTestWord().containsKey(idPerson)) {
            List<Word> listWord = getDictionaryDAO().showLimitedNumWords(chatId, 50);
            listTestWord.put(idPerson, listWord);
            ListForTestWord.setListTestWord(listTestWord);
        }
        Map<Integer, Integer> listWordForCheck = ListForTestWord.getListWordForCheck();
        Map<Integer, Enum> operations = ListForTestWord.getOperations();
        listTestWord = ListForTestWord.getListTestWord();
        int indexWord = AdditionalOperationsBot.getRandomNum(listTestWord.get(idPerson).size());
        Word word = listTestWord.get(idPerson).get(indexWord);
        operations.put(idPerson, Operations.WAITING_FOR_ANSWER);
        //ListForTestWord.setOperations(operations);
        listWordForCheck.put(idPerson, indexWord);
        sendMessage(chatId, "Как переводится слово " + word.getWord() + " ?");
    }

    private void testWordStage2(long chatId, int idPerson) {
        String answer = AdditionalOperationsBot.textFromUpdate(update);
        int indexWord = ListForTestWord.getListWordForCheck().get(idPerson);
        Word word = ListForTestWord.getListTestWord().get(idPerson).get(indexWord);
        if (answer.equalsIgnoreCase(word.getTranslate())) {
            sendMessage(chatId, "Правильно");
        } else {
            sendMessage(chatId, "Неправильно, " + word.getWord() + " переводится как " + word.getTranslate());
        }
        ListForTestWord.getOperations().replace(idPerson, Operations.WAITING_FOR_ANSWER, Operations.WAITING_FOR_INPUT);
        testWordStage1(chatId, idPerson);
    }
}
