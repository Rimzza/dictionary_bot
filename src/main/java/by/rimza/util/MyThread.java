package by.rimza.util;

import by.rimza.dao.DictionaryDAO;


import java.util.List;

public class MyThread implements Runnable {

    private final DictionaryDAO dictionaryDAO;
    private final long chatId;
    private final List<String> list;

    public MyThread(DictionaryDAO dictionaryDAO,  long chatId, List<String> list) {
        this.dictionaryDAO = dictionaryDAO;
        this.chatId = chatId;
        this.list = list;
    }

    @Override
    public void run() {
        dictionaryDAO.addWords(chatId, AdditionalOperationsBot.word(list));
    }
}
