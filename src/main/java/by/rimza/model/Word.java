package by.rimza.model;

public class Word {

    private int id;

    private  String word;

    private  String translate;



    public Word () {

    }

    public Word(int id,String word, String translate) {
        this.id = id;
        this.word = word;
        this.translate = translate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getTranslate() {
        return translate;
    }

    public void setTranslate(String translate) {
        this.translate = translate;
    }

    @Override
    public String toString() {
        return word + "-" + translate;
    }
}
