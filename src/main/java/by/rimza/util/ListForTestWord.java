package by.rimza.util;

import by.rimza.model.Word;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListForTestWord {

    private static Map<Integer, List<Word>> listTestWord = new HashMap<>();

    private static Map<Integer, Enum> operations = new HashMap<>();


    private static Map <Integer, Integer> listWordForCheck = new HashMap<>();

    public static Map<Integer, List<Word>> getListTestWord() {
        return listTestWord;
    }

    public static void setListTestWord(Map<Integer, List<Word>> listTestWord) {
        ListForTestWord.listTestWord = listTestWord;
    }

    public static Map<Integer, Enum> getOperations() {
        return operations;
    }

    public static void setOperations(Map<Integer, Enum> operations) {
        ListForTestWord.operations = operations;
    }

    public static Map<Integer, Integer> getListWordForCheck() {
        return listWordForCheck;
    }

    public static void setListWordForCheck(Map<Integer, Integer> listWordForCheck) {
        ListForTestWord.listWordForCheck = listWordForCheck;
    }



}
