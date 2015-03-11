package com.hexafarm.quizmongo2;

import android.util.Log;

import java.util.List;

public class QuestionVO {

    private String question = "";
    private final List choices;
    private int correctAnswer = -1;

    QuestionVO(String question, List choices, int correctAnswer) {
        this.question = question;
        this.choices = choices;
        this.correctAnswer = correctAnswer;
    }

    public String getQuestion() {
        return question;
    }

    public List getChoices() {
        return choices;
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }
}
