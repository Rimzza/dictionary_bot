package by.rimza.dao;

import by.rimza.model.Person;
import by.rimza.model.Word;
import by.rimza.util.CheckLanguage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Component
public class DictionaryDAO {


    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DictionaryDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void checkPerson(long idLong, String name) {
        Person person = jdbcTemplate.query("SELECT id, name FROM person WHERE id_person = ?",
                        new PersonMapper(), fromLongToInt(idLong)).
                stream().findAny().orElse(null);
        if (person == null)
            addPerson(idLong, name);
    }

    public void addWords(long idLong, List<Word> words) {
        jdbcTemplate.batchUpdate("INSERT INTO words(id_person, word, translate) VALUES (?, ?, ?)", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(@NotNull PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, getPerson(idLong).getId());
                ps.setString(2, words.get(i).getWord());
                ps.setString(3, words.get(i).getTranslate());
            }

            @Override
            public int getBatchSize() {
                return words.size();
            }
        });
    }

    public void addPerson(long idLong, String name) {
        int id = fromLongToInt(idLong);
        jdbcTemplate.update("INSERT INTO person(id_person, name) VALUES (?, ?)", fromLongToInt(idLong), name);
    }

    public void delete(long idLong, String wordToDelete) {
        if (CheckLanguage.isEn(wordToDelete)) {
            deleteByWords(idLong, wordToDelete);
        } else {
            deleteByTranslate(idLong, wordToDelete);
        }
    }

    public void deleteByWords(long idLong, String wordToDelete) {
        jdbcTemplate.update("DELETE FROM words WHERE word = ? AND id_person = (SELECT id FROM person WHERE id_person = ?)",
                wordToDelete, fromLongToInt(idLong));
    }

    public void deleteByTranslate(long id, String wordToDelete) {
        jdbcTemplate.update("DELETE FROM words WHERE translate = ? AND id_person = (SELECT id FROM person WHERE id_person = ?)",
                wordToDelete, id);
    }

    public Word showWordsByWord(long idLong, String word) {
        return jdbcTemplate.query("SELECT word, translate from words WHERE id_person = (SELECT id FROM person WHERE id_person = ?) AND word = ?",
                        new BeanPropertyRowMapper<>(Word.class), fromLongToInt(idLong), word).
                stream().findAny().orElse(null);
    }

    public Word showWordsByTranslate(long idLong, String translate) {
        return jdbcTemplate.query("SELECT word, translate from words WHERE id_person = (SELECT id FROM person WHERE id_person = ?) AND translate = ?",
                        new BeanPropertyRowMapper<>(Word.class), fromLongToInt(idLong), translate).
                stream().findAny().orElse(null);
    }

    public Word showWordFromPerson_WordsByPersonId(int idPerson) {
        return jdbcTemplate.query("SELECT word, translate from words WHERE id = (SELECT word_id FROM person_words WHERE person_id = ?)",
                        new BeanPropertyRowMapper<>(Word.class), idPerson).
                stream().findAny().orElse(null);
    }

    public List<Word> showAllWords(long idLong) {
        return jdbcTemplate.query("SELECT  word, translate from words where id_person = (SELECT id FROM person WHERE id_person = ?)",
                new BeanPropertyRowMapper<>(Word.class), fromLongToInt(idLong));
    }

    public List<Word> showLimitedNumWords(long idLong, int limit) {
        return jdbcTemplate.query("SELECT  word, translate from words where id_person = (SELECT id FROM person WHERE id_person = ?) LIMIT ?",
                new BeanPropertyRowMapper<>(Word.class), fromLongToInt(idLong), limit);
    }

    public Person getPerson(long idLong) {
        return Objects.requireNonNull(jdbcTemplate.query("SELECT id, name FROM person WHERE id_person = ?",
                        new PersonMapper(), fromLongToInt(idLong)).
                stream().findAny().orElse(null));
    }

    public int getIdWord(int idPerson, Word word) {
        return Objects.requireNonNull(jdbcTemplate.query("SELECT id, word, translate from words where id_person = ? and word = ? and translate = ?",
                        new BeanPropertyRowMapper<>(Word.class), idPerson, word.getWord(), word.getTranslate()).
                stream().findAny().orElse(null)).getId();
    }

    public void update(int idPerson, Word word) {
        jdbcTemplate.update("UPDATE words SET  word = ?, translate = ? WHERE id = (SELECT word_id FROM person_words p_w WHERE p_w.person_id = ?)",
                word.getWord(), word.getTranslate(), idPerson);
    }

    public void addPerson_words(long idPerson, int wordId) {
        jdbcTemplate.update(" INSERT INTO person_words (person_id, word_id) VALUES (?, ?)", fromLongToInt(idPerson), wordId);
    }

    public void deletePerson_words(int idPerson) {
        jdbcTemplate.update("DELETE FROM person_words WHERE person_id = ?", idPerson);
    }

    public int fromLongToInt(long i) {
        return Math.toIntExact(i);
    }


}
