package by.rimza.bot.operations;


import by.rimza.bot.Keyboard.InlineKeyboard;
import by.rimza.dao.DictionaryDAO;
import by.rimza.model.Word;
import by.rimza.util.AdditionalOperationsBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;


@Component
public class DictionaryBot extends TelegramLongPollingBot {

    private final String botUsername;
    private final String botToken;
    private final DictionaryDAO dictionaryDAO;

    private String previousCallBack;

    @Autowired
    public DictionaryBot(@Value("${botUsername}") String botUsername, @Value("${token}") String botToken, DictionaryDAO dictionaryDAO) {
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.dictionaryDAO = dictionaryDAO;
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = AdditionalOperationsBot.userId(update);
            String name = AdditionalOperationsBot.name(update);
            if (AdditionalOperationsBot.textFromUpdate(update).equals("/start")) {
                sendMessage(chatId, "Здравствуйте. Я ваш персональный словарик.");
                dictionaryDAO.checkPerson(chatId, name);
                sendMessageWithStartUpKeyboard(chatId);
            } else if (previousCallBack == null) {
                sendMessageWithStartUpKeyboard(chatId);
            } else {
                callBackListener(update);
            }

        } else if (update.hasCallbackQuery()) {
            long chatId = AdditionalOperationsBot.userIdByCallBack(update);
            String callBack = AdditionalOperationsBot.callBack(update);
            previousCallBack = callBack;
            if (!checkWords(chatId) && !callBack.equals("add_word")) {
                previousCallBack = null;
                callBack = "not_found";
            }
            callBackSendMessage(chatId, callBack);
            answerCallbackQuery(update);
        }
    }

    public void answerCallbackQuery(Update update) {
        AnswerCallbackQuery ae = AnswerCallbackQuery.builder().callbackQueryId(update.getCallbackQuery().getId()).cacheTime(1).build();
        try {
            execute(ae);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void callBackListener(Update update) {
        CallBackListener cbl = new CallBackListener(botUsername, botToken, dictionaryDAO, previousCallBack);
        cbl.checkCallBack(update);
        String call = cbl.getCallBack();
        if (call == null) {
            previousCallBack = null;
            sendMessageWithStartUpKeyboard(AdditionalOperationsBot.userId(update));
        }
    }

    public void callBackSendMessage(long chatId, String callBack) {
        switch (callBack) {
            case "add_word" -> sendMessage(chatId, "Пишите слово или слова в формате \"слово-перевод\"");

            case "delete_word" -> sendMessage(chatId, "Какое слово хотите удалить?");

            case "update_word" -> sendMessage(chatId, "Какое слово хотите изменить?");

            case "test" ->
                    sendMessage(chatId, "Сейчас будет проходить проверка вашей памяти. Для старта введите любую букву. " +
                            "Для завершения проверки напишите \"stop\" ");
            case "show_word" -> showWord(chatId);

            case "not_found" -> {
                sendMessage(chatId, "Для взаимодействия со словарем необходимо иметь хотя бы одно слово");
                sendMessageWithStartUpKeyboard(chatId);
            }
        }
    }


    private void showWord(long chatId) {
        StringBuilder stringBuilder = new StringBuilder();
        int idPerson = dictionaryDAO.getPerson(chatId).getId();
        List<Word> list = dictionaryDAO.showAllWords(chatId);
        list.forEach(word -> stringBuilder.append(word.toString()).append("\n"));
        sendMessage(chatId, stringBuilder.toString());
        sendMessageWithStartUpKeyboard(chatId);
    }

    public void sendMessageWithStartUpKeyboard(long chatId) {
        SendMessage sm = SendMessage.builder().text("Что хотите сделать?").chatId(chatId).
                replyMarkup(InlineKeyboard.createStartUp()).build();
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(long chatId, String text) {
        SendMessage sm = SendMessage.builder().text(text).chatId(chatId).build();
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    public String getBotToken() {
        return botToken;
    }

    public DictionaryDAO getDictionaryDAO() {
        return dictionaryDAO;
    }

    public String getPreviousCallBack() {
        return previousCallBack;
    }

    protected void setPreviousCallBack(String callBack) {

        previousCallBack = callBack;
    }

    public boolean checkWords(long chatId) {
        boolean result = true;
        if (dictionaryDAO.showLimitedNumWords(chatId, 1).isEmpty())
            result = false;
        return result;
    }


}
