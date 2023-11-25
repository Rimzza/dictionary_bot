
import by.rimza.config.SpringConfig;
import by.rimza.dao.DictionaryDAO;
import by.rimza.model.Word;
import by.rimza.util.AdditionalOperationsBot;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;


public class Test {

    public static void main(String[] args) {
       ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringConfig.class);
        DictionaryDAO dictionaryDAO = ctx.getBean(DictionaryDAO.class);
        Word word = new Word();
        word.setWord("test");
        word.setTranslate("тест");
        dictionaryDAO.showWordFromPerson_WordsByPersonId(2);





    }
}
