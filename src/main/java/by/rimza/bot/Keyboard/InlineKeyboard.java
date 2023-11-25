package by.rimza.bot.Keyboard;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class InlineKeyboard {

    public static InlineKeyboardMarkup createStartUp() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        List<InlineKeyboardButton> row1Inline = new ArrayList<>() {{
            add(InlineKeyboardButton.builder().text("Добавить слово").callbackData("add_word").build());
            add(InlineKeyboardButton.builder().text("Удалить слово").callbackData("delete_word").build());
        }};

        List<InlineKeyboardButton> row2Inline = new ArrayList<>() {{
            add(InlineKeyboardButton.builder().text("Изменить слово").callbackData("update_word").build());
            add(InlineKeyboardButton.builder().text("Проверить себя").callbackData("test").build());
        }};

        List<InlineKeyboardButton> row3Inline = new ArrayList<>() {{
            add(InlineKeyboardButton.builder().text("Показать все слова").callbackData("show_word").build());
        }};

        rowsInline.add(row1Inline);
        rowsInline.add(row2Inline);
        rowsInline.add(row3Inline);
        inlineKeyboardMarkup.setKeyboard(rowsInline);
        return inlineKeyboardMarkup;

    }
}
